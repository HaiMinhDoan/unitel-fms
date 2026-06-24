<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="Sự cố chuyến đi" width="1000px">
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">Báo cáo sự cố</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
              {
                icon: 'ant-design:check-outlined',
                color: 'success',
                ifShow: record.status !== 'RESOLVED',
                popConfirm: {
                  title: 'Đánh dấu sự cố này đã được giải quyết?',
                  confirm: handleResolve.bind(null, record),
                },
                tooltip: 'Giải quyết'
              },
              {
                icon: 'clarity:note-edit-line',
                onClick: handleEdit.bind(null, record),
                tooltip: 'Sửa'
              },
              {
                icon: 'ant-design:delete-outlined',
                color: 'error',
                onClick: handleDelete.bind(null, record),
                tooltip: 'Xóa'
              }
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <IncidentModal @register="registerIncidentModal" @success="handleSuccess" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { useMessage } from '@/hooks/web/useMessage';

  const { t } = useI18n();
  const { createMessage } = useMessage();
  import { ref } from 'vue';
  import { BasicModal, useModalInner, useModal } from '@/components/Modal';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { filterTripIncidents, resolveTripIncident } from '@/api/fms/trip';
  import { incidentColumns } from './incident.data';
  import IncidentModal from './IncidentModal.vue';

  const currentTripId = ref('');
  const [registerIncidentModal, { openModal: openIncidentModal }] = useModal();

  const [registerTable, { reload, setProps }] = useTable({
    title: 'Danh sách sự cố',
    api: async (params) => {
      if (!currentTripId.value) return { items: [], total: 0 };
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters: [
          { fieldName: 'tripId', operation: 'EQUALS' as const, value: currentTripId.value }
        ],
      };
      const res = await filterTripIncidents(requestParams);
      return { items: res.content, total: res.totalElements };
    },
    columns: incidentColumns,
    useSearchForm: false,
    showTableSetting: false,
    bordered: true,
    showIndexColumn: false,
    pagination: { pageSize: 10 },
    actionColumn: {
      width: 150,
      title: 'Thao tác',
      dataIndex: 'action',
    },
  });

  const [registerModal] = useModalInner(async (data) => {
    currentTripId.value = data?.record?.id;
    setProps({ searchInfo: { tripId: currentTripId.value } });
    reload();
  });

  function handleCreate() {
    openIncidentModal(true, { tripId: currentTripId.value, isUpdate: false });
  }

  function handleEdit(record: Recordable) {
    createMessage.info('Chức năng đang phát triển');
  }

  function handleDelete(record: Recordable) {
    createMessage.info('Chức năng đang phát triển');
  }

  async function handleResolve(record: Recordable) {
    try {
      await resolveTripIncident(record.id, 'Đã xử lý (đánh dấu bởi quản trị viên)');
      createMessage.success('Đã giải quyết sự cố');
      reload();
    } catch (error) {
      console.error(error);
    }
  }

  function handleSuccess() {
    reload();
  }
</script>
