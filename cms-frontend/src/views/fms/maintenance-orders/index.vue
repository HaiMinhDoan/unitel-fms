<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">Thêm Phiếu Bảo trì</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
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
    <MaintenanceModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterMaintenanceOrders } from '@/api/fms/cost';
  import MaintenanceModal from './MaintenanceModal.vue';
  import { columns, searchFormSchema } from './maintenance.data';

  const [registerModal, { openModal }] = useModal(); 
  const { t } = useI18n();

  const [registerTable, { reload }] = useTable({
    title: t('fms.maintenance.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.orderNumber) filters.push({ fieldName: 'orderNumber', operation: 'LIKE', value: params.orderNumber });
      if (params.vehicleId) filters.push({ fieldName: 'vehicle.id', operation: 'EQUALS', value: params.vehicleId });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterMaintenanceOrders(requestParams);
      return { items: res.content, total: res.totalElements };
    },
    columns,
    formConfig: {
      labelWidth: 120,
      schemas: searchFormSchema,
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

  function handleSuccess() {
    reload();
  }
</script>
