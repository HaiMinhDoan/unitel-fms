<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="Hồ sơ tài liệu Xe" width="1000px">
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" @click="handleCreate">Thêm tài liệu</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
              {
                icon: 'clarity:note-edit-line',
                onClick: handleEdit.bind(null, record),
              },
              {
                icon: record.status === 'active' ? 'ant-design:stop-outlined' : 'ant-design:check-circle-outlined',
                color: record.status === 'active' ? 'warning' : 'success',
                popConfirm: {
                  title: `Bạn có chắc chắn muốn ${record.status === 'active' ? 'tạm khóa' : 'kích hoạt'} tài liệu này?`,
                  confirm: handleChangeStatus.bind(null, record),
                },
              },
              {
                icon: 'ant-design:delete-outlined',
                color: 'error',
                popConfirm: {
                  title: 'Bạn có chắc chắn muốn xóa tài liệu này?',
                  confirm: handleDelete.bind(null, record),
                },
              },
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <DocumentModal @register="registerDocModal" @success="handleSuccess" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { useI18n } from '@/hooks/web/useI18n';
  import { useMessage } from '@/hooks/web/useMessage';

  const { t } = useI18n();
  const { createMessage } = useMessage();
  import { ref } from 'vue';
  import { BasicModal, useModalInner, useModal } from '@/components/Modal';
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { filterVehicleDocuments, deleteVehicleDocument, changeVehicleDocumentStatus } from '@/api/fms/vehicle';
  import { documentColumns } from './document.data';
  import DocumentModal from './DocumentModal.vue';

  const currentVehicleId = ref('');
  const [registerDocModal, { openModal: openDocModal }] = useModal();

  const [registerTable, { reload, setProps }] = useTable({
    title: t('fms.vehicles.title'),
    api: async (params) => {
      if (!currentVehicleId.value) return { items: [], total: 0 };
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters: [
          { fieldName: 'vehicle.id', operation: 'EQUALS' as const, value: currentVehicleId.value }
        ],
      };
      const res = await filterVehicleDocuments(requestParams);
      return { items: res.content, total: res.totalElements };
    },
    columns: documentColumns,
    useSearchForm: false,
    showTableSetting: false,
    bordered: true,
    showIndexColumn: false,
    pagination: { pageSize: 10 },
    actionColumn: {
      width: 150,
      title: 'Thao tác',
      dataIndex: 'action',
    },
  });

  const [registerModal] = useModalInner(async (data) => {
    currentVehicleId.value = data?.record?.id;
    setProps({ searchInfo: { vehicleId: currentVehicleId.value } });
    reload();
  });

  function handleCreate() {
    openDocModal(true, { vehicleId: currentVehicleId.value, isUpdate: false });
  }

  function handleEdit(record: Recordable) {
    openDocModal(true, {
      record,
      isUpdate: true,
      vehicleId: currentVehicleId.value,
    });
  }

  async function handleDelete(record: Recordable) {
    try {
      await deleteVehicleDocument(record.id);
      createMessage.success('Xóa tài liệu thành công');
      reload();
    } catch (error) {
      console.error(error);
    }
  }

  async function handleChangeStatus(record: Recordable) {
    try {
      const newStatus = record.status === 'active' ? 'inactive' : 'active';
      await changeVehicleDocumentStatus(record.id, newStatus);
      createMessage.success('Thay đổi trạng thái thành công');
      reload();
    } catch (error) {
      console.error(error);
    }
  }

  function handleSuccess() {
    reload();
  }
</script>
