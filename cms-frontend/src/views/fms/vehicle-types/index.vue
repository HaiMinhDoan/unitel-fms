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
                tooltip: t('fms.common.edit')
              }
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <VehicleTypeModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterVehicleTypes } from '@/api/fms/vehicle';
  import VehicleTypeModal from './VehicleTypeModal.vue';
  import { columns, searchFormSchema } from './vehicle-type.data';

  const [registerModal, { openModal }] = useModal(); 
  const { t } = useI18n();

  const [registerTable, { reload }] = useTable({
    title: t('routes.fms.vehicleTypes'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.code) filters.push({ fieldName: 'code', operation: 'ILIKE', value: params.code });
      if (params.nameEn) {
        filters.push({ fieldName: 'nameEn', operation: 'ILIKE', value: params.nameEn, logicType: 'OR' });
        filters.push({ fieldName: 'nameVi', operation: 'ILIKE', value: params.nameEn, logicType: 'OR' });
      }
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterVehicleTypes(requestParams);
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
