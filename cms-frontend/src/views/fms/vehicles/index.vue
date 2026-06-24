<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">{{ t('fms.common.addNew') }}</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
              {
                icon: 'carbon:map',
                onClick: handleViewMap.bind(null, record),
                tooltip: 'Xem bản đồ'
              },
              {
                icon: 'carbon:document',
                onClick: handleViewDocs.bind(null, record),
                tooltip: 'Hồ sơ tài liệu'
              },
              {
                icon: 'clarity:note-edit-line',
                onClick: handleEdit.bind(null, record),
                tooltip: 'Sửa'
              }
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <VehicleModal @register="registerModal" @success="handleSuccess" />
    <VehicleMap @register="registerMapModal" />
    <VehicleDocumentListModal @register="registerDocListModal" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterVehicles } from '@/api/fms/vehicle';
  import VehicleModal from './VehicleModal.vue';
  import VehicleMap from './VehicleMap.vue';
  import VehicleDocumentListModal from './VehicleDocumentListModal.vue';
  import { columns, searchFormSchema } from './vehicle.data';

  const [registerModal, { openModal }] = useModal(); 
  const { t } = useI18n();
  const [registerMapModal, { openModal: openMapModal }] = useModal();
  const [registerDocListModal, { openModal: openDocListModal }] = useModal();

  const [registerTable, { reload }] = useTable({
    title: t('fms.vehicles.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.plateNumber) filters.push({ fieldName: 'plateNumber', operation: 'ILIKE', value: params.plateNumber });
      if (params.brand) filters.push({ fieldName: 'brand', operation: 'ILIKE', value: params.brand });
      if (params.vehicleTypeId) filters.push({ fieldName: 'vehicleType.id', operation: 'EQUALS', value: params.vehicleTypeId });
      if (params.status) filters.push({ fieldName: 'status', operation: 'EQUALS', value: params.status });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterVehicles(requestParams);
      return { items: res.content, total: res.totalElements };
    },
    columns,
    formConfig: {
      labelWidth: 120,
      schemas: searchFormSchema,
      showAdvancedButton: true,
      alwaysShowLines: 1,
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
    actionColumn: {
      width: 120,
      title: t('fms.common.action'),
      dataIndex: 'action',
    },
  });

  function handleCreate() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record: Recordable) {
    openModal(true, { record, isUpdate: true });
  }

  function handleViewMap(record: Recordable) {
    openMapModal(true, { record });
  }

  function handleViewDocs(record: Recordable) {
    openDocListModal(true, { record });
  }

  function handleSuccess() {
    reload();
  }
</script>
