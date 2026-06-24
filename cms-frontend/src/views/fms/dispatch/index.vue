<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">Tạo yêu cầu</a-button>
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
    <DispatchModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterDispatchRequests } from '@/api/fms/dispatch';
  import DispatchModal from './DispatchModal.vue';
  import { columns, searchFormSchema } from './dispatch.data';

  const [registerModal, { openModal }] = useModal(); 
  const { t } = useI18n();

  const [registerTable, { reload }] = useTable({
    title: t('fms.dispatch.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.reqNumber) filters.push({ fieldName: 'reqNumber', operation: 'ILIKE', value: params.reqNumber });
      if (params.customerId) filters.push({ fieldName: 'customerId', operation: 'EQUALS', value: params.customerId });
      if (params.status) filters.push({ fieldName: 'status', operation: 'EQUALS', value: params.status });
      if (params.pickupLocation) filters.push({ fieldName: 'pickupLocation', operation: 'ILIKE', value: params.pickupLocation });
      if (params.dropoffLocation) filters.push({ fieldName: 'dropoffLocation', operation: 'ILIKE', value: params.dropoffLocation });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterDispatchRequests(requestParams);
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

  function handleSuccess() {
    reload();
  }
</script>
