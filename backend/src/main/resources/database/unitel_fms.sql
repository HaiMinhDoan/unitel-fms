-- ============================================================
-- FMS Unitel - PostgreSQL Schema
-- Convention: status VARCHAR(50), created_at/updated_at TIMESTAMPTZ
-- ============================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";   -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "postgis";    -- spatial types (nếu cần)

-- ============================================================
-- UTILITY: auto-update updated_at
-- ============================================================
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Macro tạo trigger updated_at cho từng bảng
-- Gọi: SELECT create_updated_at_trigger('table_name');
CREATE OR REPLACE FUNCTION create_updated_at_trigger(tbl TEXT)
    RETURNS VOID AS $$
BEGIN
EXECUTE format(
        'CREATE TRIGGER trg_%s_updated_at
         BEFORE UPDATE ON %I
         FOR EACH ROW EXECUTE FUNCTION set_updated_at()',
        tbl, tbl
        );
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- MODULE: File Attachment (lưu metadata, file thực tế ở MinIO)
-- ============================================================

CREATE TABLE file_attachments (
                                  id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                                  bucket          VARCHAR(100) NOT NULL,           -- tên bucket MinIO, vd: "fms-docs"
                                  object_key      TEXT         NOT NULL UNIQUE,    -- path trong bucket, vd: "vehicles/abc123/registration.pdf"
                                  original_name   VARCHAR(255),                    -- tên gốc của file
                                  mime_type       VARCHAR(100),                    -- loại tệp
                                  extension       VARCHAR(100),                    -- đuôi tệp
                                  size_bytes      BIGINT,
                                  entity_type     VARCHAR(100),                    -- vehicle_documents | driver_documents | epod_photo | epod_sign
                                  entity_id       UUID,
                                  uploaded_by     UUID,                            -- FK -> users(id), set sau khi tạo bảng user
                                  status          VARCHAR(50)  NOT NULL DEFAULT 'active',  -- active | deleted
                                  created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                  updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('file_attachments');

CREATE INDEX idx_file_attachment_entity ON file_attachments (entity_type, entity_id);
CREATE INDEX idx_file_attachment_uploader ON file_attachments (uploaded_by);

-- ============================================================
-- MODULE: Tổ chức & Phân quyền
-- ============================================================

CREATE TABLE organizations (
                               id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                               code            VARCHAR(50)  NOT NULL UNIQUE,
                               name_en         VARCHAR(255) NOT NULL,
                               name_vi         VARCHAR(255),
                               name_lo         VARCHAR(255),
                               org_type        VARCHAR(50)  NOT NULL,           -- HQ | BRANCH | PARTNER
                               parent_id       UUID         REFERENCES organizations(id),
                               region          VARCHAR(100),
                               timezone        VARCHAR(50)  NOT NULL DEFAULT 'Asia/Vientiane',
                               status          VARCHAR(50)  NOT NULL DEFAULT 'active',
                               created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                               updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('organizations');

-- ----

CREATE TABLE roles (
                       id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       code        VARCHAR(50)  NOT NULL UNIQUE,
                       name        VARCHAR(100) NOT NULL,
                       description TEXT,
                       status      VARCHAR(50)  NOT NULL DEFAULT 'active',
                       created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('roles');

-- ----

CREATE TABLE users (
                       id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       org_id      UUID        REFERENCES organizations(id),
                       username    VARCHAR(100) NOT NULL UNIQUE,
                       email       VARCHAR(255) NOT NULL UNIQUE,
                       full_name   VARCHAR(255) NOT NULL,
                       phone       VARCHAR(30),
                       sso_subject VARCHAR(255) UNIQUE,               -- subject từ SSO/OIDC
                       locale      VARCHAR(10)  NOT NULL DEFAULT 'lo',
                       status      VARCHAR(50)  NOT NULL DEFAULT 'active',
                       last_login_at TIMESTAMPTZ,
                       created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('users');

-- ----

CREATE TABLE user_roles (
                            id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id     UUID        NOT NULL REFERENCES users(id),
                            role_id     UUID        NOT NULL REFERENCES roles(id),
                            org_id      UUID        REFERENCES organizations(id),   -- phân quyền per-org, NULL = role toàn cục (SYSTEM_ADMIN)
                            granted_by  UUID        REFERENCES users(id),
                            status      VARCHAR(50)  NOT NULL DEFAULT 'active',
                            created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                            updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                            UNIQUE (user_id, role_id, org_id)
);

-- Chặn trùng lặp khi org_id = NULL (UNIQUE constraint gốc không tự chặn được NULL trùng NULL)
CREATE UNIQUE INDEX uq_user_roles_global
    ON user_roles (user_id, role_id)
    WHERE org_id IS NULL;

SELECT create_updated_at_trigger('user_roles');

-- ----

CREATE TABLE audit_logs (
                            id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id     UUID        REFERENCES users(id),
                            action      VARCHAR(100) NOT NULL,              -- CREATE | UPDATE | DELETE | DISPATCH | OVERRIDE
                            entity_type VARCHAR(100) NOT NULL,
                            entity_id   UUID,
                            old_values  JSONB,
                            new_values  JSONB,
                            ip_address  VARCHAR(45),
                            status      VARCHAR(50)  NOT NULL DEFAULT 'recorded',
                            created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                            updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('audit_logs');

CREATE INDEX idx_audit_log_entity  ON audit_logs (entity_type, entity_id);
CREATE INDEX idx_audit_log_user    ON audit_logs (user_id);
CREATE INDEX idx_audit_log_created ON audit_logs (created_at DESC);

-- ----

CREATE TABLE system_configs (
                                id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                config_key  VARCHAR(100) NOT NULL UNIQUE,
                                config_value JSONB       NOT NULL,
                                description TEXT,
                                updated_by  UUID        REFERENCES users(id),
                                status      VARCHAR(50)  NOT NULL DEFAULT 'active',
                                created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('system_configs');

-- ----

CREATE TABLE notifications (
                               id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id     UUID        NOT NULL REFERENCES users(id),
                               channel     VARCHAR(50)  NOT NULL DEFAULT 'in_app',  -- in_app | email | sms | push
                               title       VARCHAR(255) NOT NULL,
                               body        TEXT        NOT NULL,
                               entity_type VARCHAR(100),
                               entity_id   UUID,
                               is_read     BOOLEAN     NOT NULL DEFAULT false,
                               status      VARCHAR(50)  NOT NULL DEFAULT 'sent',
                               created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                               updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('notifications');

CREATE INDEX idx_notification_user ON notifications (user_id, is_read, created_at DESC);

-- ============================================================
-- MODULE: Phương tiện
-- ============================================================

CREATE TABLE vehicle_types (
                               id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                               code            VARCHAR(50)  NOT NULL UNIQUE,
                               name_en         VARCHAR(100) NOT NULL,
                               name_vi         VARCHAR(100),
                               name_lo         VARCHAR(100),
                               max_load_ton    NUMERIC(8,2),
                               seat_count      SMALLINT,
                               cargo_type      VARCHAR(50),                    -- dry | refrigerated | tanker | flatbed
                               status          VARCHAR(50)  NOT NULL DEFAULT 'active',
                               created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                               updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('vehicle_types');

-- ----

CREATE TABLE vehicles (
                          id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                          org_id              UUID        NOT NULL REFERENCES organizations(id),
                          vehicle_type_id     UUID        NOT NULL REFERENCES vehicle_types(id),
                          plate_number        VARCHAR(30)  NOT NULL UNIQUE,
                          brand               VARCHAR(100),
                          model               VARCHAR(100),
                          manufacture_year    SMALLINT,
                          ownership_type      VARCHAR(30)  NOT NULL DEFAULT 'owned',  -- owned | leased
                          load_capacity_ton   NUMERIC(8,2),
                          current_odometer    NUMERIC(10,2) DEFAULT 0,
                          status              VARCHAR(50)  NOT NULL DEFAULT 'active', -- active | maintenance | inactive
                          status_changed_at   TIMESTAMPTZ,
                          created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('vehicles');

CREATE INDEX idx_vehicle_org    ON vehicles (org_id);
CREATE INDEX idx_vehicle_status ON vehicles (status);

-- ----

CREATE TABLE vehicle_documents (
                                   id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                   vehicle_id  UUID        NOT NULL REFERENCES vehicles(id),
                                   doc_type    VARCHAR(50)  NOT NULL,              -- registration | insurance | inspection
                                   doc_number  VARCHAR(100),
                                   issue_date  DATE,
                                   expiry_date DATE,
                                   status      VARCHAR(50)  NOT NULL DEFAULT 'valid',  -- valid | expiring_soon | expired | missing
                                   created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                   updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('vehicle_documents');

CREATE INDEX idx_vehicle_doc_vehicle  ON vehicle_documents (vehicle_id);
CREATE INDEX idx_vehicle_doc_expiry   ON vehicle_documents (expiry_date) WHERE status != 'expired';

-- ----

CREATE TABLE vehicle_healths (
                                 id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                 vehicle_id          UUID        NOT NULL REFERENCES vehicles(id),
                                 health_score        SMALLINT    NOT NULL CHECK (health_score BETWEEN 0 AND 100),
                                 risk_level          VARCHAR(20)  NOT NULL,       -- low | medium | high | critical
                                 component_scores    JSONB,                       -- { "tire": 80, "brake": 55, "fuel_eff": 70 }
                                 is_dispatch_blocked BOOLEAN     NOT NULL DEFAULT false,
                                 notes               TEXT,
                                 assessed_by         UUID        REFERENCES users(id),
                                 status              VARCHAR(50)  NOT NULL DEFAULT 'active',
                                 created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                 updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('vehicle_healths');

CREATE INDEX idx_vehicle_health_vehicle ON vehicle_healths (vehicle_id, created_at DESC);

-- ----

CREATE TABLE maintenance_orders (
                                    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                    vehicle_id      UUID        NOT NULL REFERENCES vehicles(id),
                                    created_by      UUID        REFERENCES users(id),
                                    order_type      VARCHAR(50)  NOT NULL,           -- scheduled | corrective | emergency
                                    scheduled_date  DATE,
                                    completed_date  DATE,
                                    total_cost      NUMERIC(15,2),
                                    notes           TEXT,
                                    status          VARCHAR(50)  NOT NULL DEFAULT 'pending',  -- pending | in_progress | completed | cancelled
                                    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('maintenance_orders');

CREATE INDEX idx_maintenance_vehicle ON maintenance_orders (vehicle_id);

-- ----

CREATE TABLE maintenance_items (
                                   id                   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                   maintenance_order_id UUID        NOT NULL REFERENCES maintenance_orders(id),
                                   item_name            VARCHAR(255) NOT NULL,
                                   part_code            VARCHAR(100),
                                   quantity             SMALLINT    NOT NULL DEFAULT 1,
                                   unit_cost            NUMERIC(15,2),
                                   total_cost           NUMERIC(15,2),
                                   status               VARCHAR(50)  NOT NULL DEFAULT 'pending',
                                   created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                   updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('maintenance_items');

-- ============================================================
-- MODULE: Tài xế
-- ============================================================

CREATE TABLE drivers (
                         id                   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                         org_id               UUID        NOT NULL REFERENCES organizations(id),
                         user_id              UUID        UNIQUE REFERENCES users(id),
                         employee_code        VARCHAR(50)  UNIQUE,
                         full_name            VARCHAR(255) NOT NULL,
                         phone                VARCHAR(30),
                         license_class        VARCHAR(20),
                         license_number       VARCHAR(50),
                         is_dispatch_eligible BOOLEAN     NOT NULL DEFAULT true,
                         status               VARCHAR(50)  NOT NULL DEFAULT 'active',  -- active | inactive | suspended
                         created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('drivers');

CREATE INDEX idx_driver_org    ON drivers (org_id);
CREATE INDEX idx_driver_status ON drivers (status, is_dispatch_eligible);

-- ----

CREATE TABLE driver_documents (
                                  id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                  driver_id   UUID        NOT NULL REFERENCES drivers(id),
                                  doc_type    VARCHAR(50)  NOT NULL,   -- license | health_cert | labor_contract | training_cert
                                  doc_number  VARCHAR(100),
                                  issue_date  DATE,
                                  expiry_date DATE,
                                  status      VARCHAR(50)  NOT NULL DEFAULT 'valid',  -- valid | expiring_soon | expired | missing
                                  created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                  updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('driver_documents');

CREATE INDEX idx_driver_doc_driver ON driver_documents (driver_id);
CREATE INDEX idx_driver_doc_expiry ON driver_documents (expiry_date) WHERE status != 'expired';

-- ============================================================
-- MODULE: Khách hàng
-- ============================================================

CREATE TABLE customers (
                           id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                           org_id          UUID        NOT NULL REFERENCES organizations(id),
                           code            VARCHAR(50)  NOT NULL UNIQUE,
                           name            VARCHAR(255) NOT NULL,
                           contact_person  VARCHAR(255),
                           phone           VARCHAR(30),
                           email           VARCHAR(255),
                           address         TEXT,
                           status          VARCHAR(50)  NOT NULL DEFAULT 'active',
                           created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                           updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('customers');

-- ============================================================
-- MODULE: Điều xe & Chuyến
-- ============================================================

CREATE TABLE dispatch_requests (
                                   id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                   org_id                  UUID        NOT NULL REFERENCES organizations(id),
                                   customer_id             UUID        REFERENCES customers(id),
                                   created_by              UUID        REFERENCES users(id),
                                   req_number              VARCHAR(50)  NOT NULL UNIQUE,
                                   source_channel          VARCHAR(50)  NOT NULL DEFAULT 'manual',  -- manual | web | email | api | partners
                                   origin_address          TEXT        NOT NULL,
                                   origin_lat              NUMERIC(10,7),
                                   origin_lng              NUMERIC(10,7),
                                   dest_address            TEXT        NOT NULL,
                                   dest_lat                NUMERIC(10,7),
                                   dest_lng                NUMERIC(10,7),
                                   cargo_weight_ton        NUMERIC(8,2),
                                   cargo_type              VARCHAR(100),
                                   cargo_notes             TEXT,
                                   priority                VARCHAR(20)  NOT NULL DEFAULT 'medium',  -- low | medium | high
                                   requested_pickup_at     TIMESTAMPTZ,
                                   requested_delivery_at   TIMESTAMPTZ,
                                   status                  VARCHAR(50)  NOT NULL DEFAULT 'new',
    -- new | processing | assigned | pending | completed | cancelled
                                   created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                   updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('dispatch_requests');

CREATE INDEX idx_dispatch_req_org    ON dispatch_requests (org_id, status);
CREATE INDEX idx_dispatch_req_number ON dispatch_requests (req_number);

-- ----

CREATE TABLE dispatch_assignments (
                                      id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                      dispatch_request_id UUID        NOT NULL REFERENCES dispatch_requests(id),
                                      vehicle_id          UUID        NOT NULL REFERENCES vehicles(id),
                                      driver_id           UUID        NOT NULL REFERENCES drivers(id),
                                      assigned_by         UUID        REFERENCES users(id),
                                      assignment_type     VARCHAR(30)  NOT NULL DEFAULT 'manual',  -- manual | auto | partners
                                      pre_check_passed    BOOLEAN     NOT NULL DEFAULT false,
                                      pre_check_details   JSONB,
    -- { "load_ok": true, "vehicle_docs_ok": true, "health_ok": false, "driver_docs_ok": true }
                                      override_reason     TEXT,                                    -- nếu chặn nhưng vẫn cho qua
                                      override_by         UUID        REFERENCES users(id),
                                      status              VARCHAR(50)  NOT NULL DEFAULT 'active',  -- active | cancelled | completed
                                      created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                      updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('dispatch_assignments');

CREATE INDEX idx_dispatch_assign_vehicle ON dispatch_assignments (vehicle_id);
CREATE INDEX idx_dispatch_assign_driver  ON dispatch_assignments (driver_id);

-- ----

CREATE TABLE trips (
                       id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       dispatch_assignment_id  UUID        NOT NULL UNIQUE REFERENCES dispatch_assignments(id),
                       trip_number             VARCHAR(50)  NOT NULL UNIQUE,
                       progress_pct            SMALLINT    NOT NULL DEFAULT 0,
                       planned_start_at        TIMESTAMPTZ,
                       planned_end_at          TIMESTAMPTZ,
                       actual_start_at         TIMESTAMPTZ,
                       actual_end_at           TIMESTAMPTZ,
                       eta                     TIMESTAMPTZ,
                       sla_at_risk             BOOLEAN     NOT NULL DEFAULT false,
                       total_distance_km       NUMERIC(10,2),
                       total_cost              NUMERIC(15,2),
                       status                  VARCHAR(50)  NOT NULL DEFAULT 'pending',
    -- pending | in_progress | delayed | completed | cancelled
                       created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('trips');

CREATE INDEX idx_trip_status ON trips (status, sla_at_risk);

-- ----

CREATE TABLE trip_stops (
                            id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                            trip_id         UUID        NOT NULL REFERENCES trips(id),
                            stop_order      SMALLINT    NOT NULL,
                            stop_type       VARCHAR(30)  NOT NULL,       -- pickup | delivery | waypoint | checkpoint
                            address         TEXT        NOT NULL,
                            lat             NUMERIC(10,7),
                            lng             NUMERIC(10,7),
                            planned_arrival TIMESTAMPTZ,
                            actual_arrival  TIMESTAMPTZ,
                            notes           TEXT,
                            status          VARCHAR(50)  NOT NULL DEFAULT 'pending',  -- pending | arrived | completed | skipped
                            created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                            updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                            UNIQUE (trip_id, stop_order)
);

SELECT create_updated_at_trigger('trip_stops');

CREATE INDEX idx_trip_stop_trip ON trip_stops (trip_id, stop_order);

-- ----

CREATE TABLE trip_incidents (
                                id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                trip_id         UUID        NOT NULL REFERENCES trips(id),
                                reported_by     UUID        REFERENCES users(id),
                                incident_type   VARCHAR(50)  NOT NULL,       -- breakdown | accident | delay | route_deviation | other
                                description     TEXT,
                                metadata        JSONB,
                                resolved_by     UUID        REFERENCES users(id),
                                resolved_at     TIMESTAMPTZ,
                                status          VARCHAR(50)  NOT NULL DEFAULT 'open',   -- open | in_progress | resolved | closed
                                created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('trip_incidents');

CREATE INDEX idx_trip_incident_trip ON trip_incidents (trip_id);

-- ----

CREATE TABLE fuel_logs (
                           id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                           vehicle_id              UUID        NOT NULL REFERENCES vehicles(id),
                           trip_id                 UUID        REFERENCES trips(id),
                           logged_by               UUID        REFERENCES users(id),
                           liters_added            NUMERIC(8,2) NOT NULL,
                           fuel_cost               NUMERIC(15,2),
                           odometer_at_fill        NUMERIC(10,2),
                           reported_consumption    NUMERIC(8,2),
                           telemetry_consumption   NUMERIC(8,2),
                           anomaly_flagged         BOOLEAN     NOT NULL DEFAULT false,
                           anomaly_notes           TEXT,
                           status                  VARCHAR(50)  NOT NULL DEFAULT 'recorded',
                           created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
                           updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('fuel_logs');

CREATE INDEX idx_fuel_log_vehicle ON fuel_logs (vehicle_id, created_at DESC);

-- ============================================================
-- MODULE: GPS & Telemetry
-- ============================================================

CREATE TABLE gps_positions (
    -- Composite PK (vehicle_id, recorded_at) — xem comment
    -- Nên dùng TimescaleDB hypertable partition by recorded_at
                               vehicle_id      UUID        NOT NULL REFERENCES vehicles(id),
                               recorded_at     TIMESTAMPTZ  NOT NULL,
                               trip_id         UUID        REFERENCES trips(id),
                               lat             DOUBLE PRECISION NOT NULL,
                               lng             DOUBLE PRECISION NOT NULL,
                               speed_kmh       REAL,
                               heading         REAL,
                               altitude        REAL,
                               accuracy_m      REAL,
                               source          VARCHAR(20)  NOT NULL DEFAULT 'device',  -- device | driver_app
                               status          VARCHAR(50)  NOT NULL DEFAULT 'received',
                               created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                               updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                               PRIMARY KEY (vehicle_id, recorded_at)
);

-- TimescaleDB:
-- SELECT create_hypertable('gps_positions', 'recorded_at', chunk_time_interval => INTERVAL '1 day');

CREATE INDEX idx_gps_vehicle_time ON gps_positions (vehicle_id, recorded_at DESC);
CREATE INDEX idx_gps_trip        ON gps_positions (trip_id, recorded_at DESC) WHERE trip_id IS NOT NULL;

-- ----

CREATE TABLE gps_alerts (
                            id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                            vehicle_id          UUID        NOT NULL REFERENCES vehicles(id),
                            trip_id             UUID        REFERENCES trips(id),
                            alert_type          VARCHAR(50)  NOT NULL,
    -- speeding | route_deviation | abnormal_stop | geofence_enter | geofence_exit | sla_risk
                            details             JSONB,
                            is_acknowledged     BOOLEAN     NOT NULL DEFAULT false,
                            acknowledged_by     UUID        REFERENCES users(id),
                            acknowledged_at     TIMESTAMPTZ,
                            triggered_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
                            status              VARCHAR(50)  NOT NULL DEFAULT 'open',  -- open | acknowledged | resolved
                            created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
                            updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('gps_alerts');

CREATE INDEX idx_gps_alert_vehicle ON gps_alerts (vehicle_id, triggered_at DESC);
CREATE INDEX idx_gps_alert_open    ON gps_alerts (status) WHERE status = 'open';

-- ----

CREATE TABLE geofences (
                           id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                           org_id      UUID        NOT NULL REFERENCES organizations(id),
                           name        VARCHAR(255) NOT NULL,
                           fence_type  VARCHAR(30)  NOT NULL DEFAULT 'polygon',  -- polygon | circle
                           coordinates JSONB       NOT NULL,   -- GeoJSON geometry
                           radius_m    NUMERIC(10,2),          -- dùng khi fence_type = circle
                           status      VARCHAR(50)  NOT NULL DEFAULT 'active',
                           created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                           updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('geofences');

-- ============================================================
-- MODULE: Đối tác vận tải
-- ============================================================

CREATE TABLE partners (
                          id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                          org_id          UUID        NOT NULL REFERENCES organizations(id),
                          code            VARCHAR(50)  NOT NULL UNIQUE,
                          name            VARCHAR(255) NOT NULL,
                          contact_person  VARCHAR(255),
                          phone           VARCHAR(30),
                          email           VARCHAR(255),
                          address         TEXT,
                          rating          VARCHAR(20)  DEFAULT 'unrated',  -- unrated | bronze | silver | gold
                          on_time_rate    NUMERIC(5,2),                    -- %
                          status          VARCHAR(50)  NOT NULL DEFAULT 'active',
                          created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('partners');

-- ----

CREATE TABLE partner_vehicles (
                                  id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                  partner_id          UUID        NOT NULL REFERENCES partners(id),
                                  vehicle_type_id     UUID        NOT NULL REFERENCES vehicle_types(id),
                                  plate_number        VARCHAR(30)  NOT NULL UNIQUE,
                                  load_capacity_ton   NUMERIC(8,2),
                                  status              VARCHAR(50)  NOT NULL DEFAULT 'available',  -- available | on_trip | inactive
                                  created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                  updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('partner_vehicles');

-- ----

CREATE TABLE partner_drivers (
                                 id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                 partner_id      UUID        NOT NULL REFERENCES partners(id),
                                 full_name       VARCHAR(255) NOT NULL,
                                 phone           VARCHAR(30),
                                 license_class   VARCHAR(20),
                                 license_number  VARCHAR(50),
                                 status          VARCHAR(50)  NOT NULL DEFAULT 'active',
                                 created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                 updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

SELECT create_updated_at_trigger('partner_drivers');

-- ----

CREATE TABLE partner_performances (
                                      id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                      partner_id      UUID        NOT NULL REFERENCES partners(id),
                                      period_month    CHAR(7)     NOT NULL,       -- YYYY-MM
                                      total_trips     INT         NOT NULL DEFAULT 0,
                                      on_time_trips   INT         NOT NULL DEFAULT 0,
                                      avg_cost        NUMERIC(15,2),
                                      total_revenue   NUMERIC(15,2),
                                      notes           TEXT,
                                      status          VARCHAR(50)  NOT NULL DEFAULT 'final',  -- draft | final
                                      created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                      updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                      UNIQUE (partner_id, period_month)
);

SELECT create_updated_at_trigger('partner_performances');

INSERT INTO roles (code, name, description) VALUES
                                                ('SYSTEM_ADMIN',   'Quản trị hệ thống',           'Cấu hình hệ thống, phân quyền, quản lý tổ chức, danh mục, nhật ký'),
                                                ('DISPATCHER',     'Điều phối viên',               'Tiếp nhận yêu cầu, gán xe/tài xế, giám sát chuyến, xử lý phát sinh'),
                                                ('OPS_MANAGER',    'Quản lý vận hành / điều hành',  'Theo dõi dashboard, KPI, chi phí, ra quyết định điều hành'),
                                                ('FLEET_MANAGER',  'Quản lý phương tiện & kỹ thuật','Quản lý hồ sơ xe, sức khỏe xe, lịch bảo trì, nhiên liệu, phụ tùng'),
                                                ('HR_LEGAL',       'Nhân sự / pháp chế',            'Theo dõi giấy tờ tài xế, hợp đồng, đào tạo, cảnh báo hết hạn'),
                                                ('DRIVER',         'Tài xế',                        'Nhận chuyến, cập nhật trạng thái, dẫn đường, báo cáo sự cố qua app di động'),
                                                ('PARTNER_MANAGER','Quản lý đối tác',               'Quản lý nhà vận tải, năng lực, so sánh phương án, đối soát');