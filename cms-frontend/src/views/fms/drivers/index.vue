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
    <DriverModal @register="registerModal" @success="handleSuccess" />
    <DriverDocumentListModal @register="registerDocListModal" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterDrivers } from '@/api/fms/driver';
  import DriverModal from './DriverModal.vue';
  import DriverDocumentListModal from './DriverDocumentListModal.vue';
  import { columns, searchFormSchema } from './driver.data';

  const [registerModal, { openModal }] = useModal(); 
  const [registerDocListModal, { openModal: openDocListModal }] = useModal();
  const { t } = useI18n();

  const [registerTable, { reload }] = useTable({
    title: t('fms.drivers.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.fullName) filters.push({ fieldName: 'fullName', operation: 'ILIKE', value: params.fullName });
      if (params.licenseNumber) filters.push({ fieldName: 'licenseNumber', operation: 'ILIKE', value: params.licenseNumber });
      if (params.licenseClass) filters.push({ fieldName: 'licenseClass', operation: 'ILIKE', value: params.licenseClass });
      if (params.status) filters.push({ fieldName: 'status', operation: 'EQUALS', value: params.status });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterDrivers(requestParams);
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
      width: 150,
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

  function handleViewDocs(record: Recordable) {
    openDocListModal(true, { record });
  }

  function handleSuccess() {
    reload();
  }
</script>
