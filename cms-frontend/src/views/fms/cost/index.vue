<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">Thêm nhật ký nhiên liệu</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
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
    <CostModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterFuelLogs } from '@/api/fms/cost';
  import CostModal from './CostModal.vue';
  import { columns, searchFormSchema } from './cost.data';
  import { useMessage } from '@/hooks/web/useMessage';

  const [registerModal, { openModal }] = useModal(); 
  const { t } = useI18n();

  const { createMessage } = useMessage();

  const [registerTable, { reload }] = useTable({
    title: t('fms.cost.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.vehicleId) filters.push({ fieldName: 'vehicle.id', operation: 'EQUALS', value: params.vehicle.id });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterFuelLogs(requestParams);
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
      width: 150,
      title: t('fms.common.action'),
      dataIndex: 'action',
    },
  });

  function handleCreate() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record: Recordable) {
    createMessage.info('Chức năng đang phát triển');
  }

  function handleDelete(record: Recordable) {
    createMessage.info('Chức năng đang phát triển');
  }

  function handleSuccess() {
    reload();
  }
</script>
