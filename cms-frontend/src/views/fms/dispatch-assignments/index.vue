<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">Thêm Phân công</a-button>
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
    <AssignmentModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterDispatchAssignments } from '@/api/fms/dispatch';
  import AssignmentModal from './AssignmentModal.vue';
  import { columns, searchFormSchema } from './assignment.data';

  const [registerModal, { openModal }] = useModal(); 
  const { t } = useI18n();

  const [registerTable, { reload }] = useTable({
    title: t('fms.dispatchAssignments.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.dispatchRequestId) filters.push({ fieldName: 'dispatchRequest.id', operation: 'EQUALS', value: params.dispatchRequestId });
      if (params.assignmentType) filters.push({ fieldName: 'assignmentType', operation: 'EQUALS', value: params.assignmentType });
      if (params.status) filters.push({ fieldName: 'status', operation: 'EQUALS', value: params.status });
      if (params.vehicleId) filters.push({ fieldName: 'vehicle.id', operation: 'EQUALS', value: params.vehicleId });
      if (params.driverId) filters.push({ fieldName: 'driver.id', operation: 'EQUALS', value: params.driverId });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterDispatchAssignments(requestParams);
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
