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
                icon: 'clarity:note-edit-line',
                onClick: handleEdit.bind(null, record),
                tooltip: 'Sửa'
              }
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <CustomerModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterCustomers } from '@/api/fms/customer';
  import CustomerModal from './CustomerModal.vue';
  import { columns, searchFormSchema } from './customer.data';

  const [registerModal, { openModal }] = useModal(); 
  const { t } = useI18n();

  const [registerTable, { reload }] = useTable({
    title: t('fms.customers.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.name) filters.push({ fieldName: 'name', operation: 'LIKE', value: params.name });
      if (params.code) filters.push({ fieldName: 'code', operation: 'LIKE', value: params.code });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterCustomers(requestParams);
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
