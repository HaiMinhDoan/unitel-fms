<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
    </BasicTable>
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';

  const { t } = useI18n();
  import { BasicTable, useTable } from '@/components/Table';
  import { filterPartnerDrivers } from '@/api/fms/partner';
  import { columns, searchFormSchema } from '../drivers/driver.data';

  const [registerTable] = useTable({
    title: t('fms.routes.partnerDrivers'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.fullName) filters.push({ fieldName: 'fullName', operation: 'LIKE', value: params.fullName });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterPartnerDrivers(requestParams);
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
