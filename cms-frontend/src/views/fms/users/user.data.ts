import { BasicColumn, FormSchema } from '@/components/Table';
import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();

export const columns: BasicColumn[] = [
  {
    title: t('fms.common.id'),
    dataIndex: 'id',
    width: 200,
    ifShow: false,
  },
  {
    title: t('fms.users.username'),
    dataIndex: 'username',
    width: 150,
  },
  {
    title: t('fms.common.email'),
    dataIndex: 'email',
    width: 200,
  },
  {
    title: t('fms.common.phone'),
    dataIndex: 'phone',
    width: 150,
  },
  {
    title: t('fms.common.status'),
    dataIndex: 'status',
    width: 120,
  },
  {
    title: t('fms.common.createdAt'),
    dataIndex: 'createdAt',
    width: 180,
  },
  {
    title: t('fms.users.lastLoginAt'),
    dataIndex: 'lastLoginAt',
    width: 180,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'username',
    label: t('fms.users.username'),
    component: 'Input',
    colProps: { span: 8 },
  },
];

export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: t('fms.common.id'),
    component: 'Input',
    show: false,
  },
  {
    field: 'username',
    label: t('fms.users.username'),
    component: 'Input',
    required: true,
  },
  {
    field: 'email',
    label: t('fms.common.email'),
    component: 'Input',
    required: true,
  },
  {
    field: 'phone',
    label: t('fms.common.phone'),
    component: 'Input',
  },
  {
    field: 'status',
    label: t('fms.common.status'),
    component: 'Select',
    componentProps: {
      options: [
        { label: t('fms.common.active'), value: 'active' },
        { label: t('fms.common.inactive'), value: 'inactive' },
      ],
    },
  },
];
