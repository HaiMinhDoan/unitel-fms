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
              },
              {
                icon: 'ant-design:delete-outlined',
                color: 'error',
                popConfirm: {
                  title: t('fms.common.deleteConfirm'),
                  confirm: handleDelete.bind(null, record),
                },
              },
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <UserModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '@/components/Table';
  import { useModal } from '@/components/Modal';
  import { useI18n } from '@/hooks/web/useI18n';
  import { filterUsers, softDeleteUser } from '@/api/fms/user';
  import UserModal from './UserModal.vue';
  import { columns, searchFormSchema } from './user.data';

  const [registerModal, { openModal }] = useModal();
  const { t } = useI18n();

  const [registerTable, { reload }] = useTable({
    title: t('fms.users.title'),
    api: async (params) => {
      const filters: any[] = [];
      if (params.username) filters.push({ fieldName: 'username', operation: 'LIKE', value: params.username });
      
      const requestParams = {
        page: (params.page || 1) - 1,
        size: params.pageSize || 10,
        filters,
      };
      const res = await filterUsers(requestParams);
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
    openModal(true, {
      isUpdate: false,
    });
  }

  function handleEdit(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }

  async function handleDelete(record: Recordable) {
    await softDeleteUser(record.id);
    reload();
  }

  function handleSuccess() {
    reload();
  }
</script>
