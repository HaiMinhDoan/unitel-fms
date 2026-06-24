<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
              {
                icon: 'carbon:map',
                onClick: handleViewMap.bind(null, record),
                tooltip: 'Xem Bản Đồ'
              },
              {
                icon: 'carbon:warning',
                color: 'error',
                onClick: handleViewIncidents.bind(null, record),
                tooltip: 'Sự cố chuyến đi'
              }
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <TripMapModal @register="registerMapModal" />
    <IncidentListModal @register="registerIncidentListModal" />
  </div>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';

  const { t } = useI18n();
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { filterTrips } from '@/api/fms/trip';
  import TripMapModal from './TripMapModal.vue';
  import IncidentListModal from './IncidentListModal.vue';
  import { columns, searchFormSchema } from './trip.data';

  const [registerMapModal, { openModal: openMapModal }] = useModal();
  const [registerIncidentListModal, { openModal: openIncidentListModal }] = useModal();

  const [registerTable] = useTable({
    title: t('fms.trips.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.dispatchAssignmentId) filters.push({ fieldName: 'dispatchAssignmentId', operation: 'LIKE', value: params.dispatchAssignmentId });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterTrips(requestParams);
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
      title: 'Hành trình',
      dataIndex: 'action',
    },
  });

  function handleViewMap(record: Recordable) {
    openMapModal(true, { record });
  }

  function handleViewIncidents(record: Recordable) {
    openIncidentListModal(true, { record });
  }

</script>
