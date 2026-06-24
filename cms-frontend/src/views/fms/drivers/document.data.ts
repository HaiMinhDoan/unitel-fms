import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

import { renderAttachments } from '@/utils/filePreview';

export const documentColumns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.drivers.docType'), dataIndex: 'docType', width: 150 },
  { title: t('fms.drivers.docNumber'), dataIndex: 'docNumber', width: 150 },
  { title: t('fms.drivers.issueDate'), dataIndex: 'issueDate', width: 150 },
  { title: t('fms.drivers.expiryDate'), dataIndex: 'expiryDate', width: 150 },
  { title: t('fms.common.publicUrl'), dataIndex: 'attachments', width: 200, customRender: ({ record }) => renderAttachments(record.attachments) },
];

export const documentFormSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'driverId', label: t('fms.drivers.driverId'), component: 'Input', show: false },
  { field: 'docType', label: t('fms.drivers.docType'), component: 'Select', componentProps: { options: [{ label: 'GPLX', value: 'license' }, { label: 'CCCD/CMND', value: 'id_card' }, { label: 'Giấy khám sức khỏe', value: 'health_cert' }] }, required: true },
  { field: 'docNumber', label: t('fms.drivers.docNumber'), component: 'Input', required: true },
  { field: 'issueDate', label: t('fms.drivers.issueDate'), component: 'DatePicker', componentProps: { format: 'YYYY-MM-DD', valueFormat: 'YYYY-MM-DD' } },
  { field: 'expiryDate', label: t('fms.drivers.expiryDate'), component: 'DatePicker', componentProps: { format: 'YYYY-MM-DD', valueFormat: 'YYYY-MM-DD' } },
  { field: 'files', label: 'Tài liệu (Files)', slot: 'fileUpload' },
];
