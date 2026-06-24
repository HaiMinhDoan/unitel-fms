<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <!-- Read-only view for partner vehicles for simplicity, or we can use similar action tools -->
    </BasicTable>
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';

  const { t } = useI18n();
  import { BasicTable, useTable } from '@/components/Table';
  import { filterPartnerVehicles } from '@/api/fms/partner';
  import { columns, searchFormSchema } from '../vehicles/vehicle.data';


  const [registerTable] = useTable({
    title: t('fms.routes.partnerVehicles'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.plateNumber) filters.push({ fieldName: 'plateNumber', operation: 'LIKE', value: params.plateNumber });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterPartnerVehicles(requestParams);
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
    actionColumn: undefined,
  });
</script>
