import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

import { renderAttachments } from '@/utils/filePreview';

export const documentColumns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.vehicles.docType'), dataIndex: 'docType', width: 150 },
  { title: t('fms.vehicles.docNumber'), dataIndex: 'docNumber', width: 150 },
  { title: t('fms.vehicles.issueDate'), dataIndex: 'issueDate', width: 150 },
  { title: t('fms.vehicles.expiryDate'), dataIndex: 'expiryDate', width: 150 },
  { title: t('fms.common.publicUrl'), dataIndex: 'attachments', width: 200, customRender: ({ record }) => renderAttachments(record.attachments) },
];

export const documentFormSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'vehicleId', label: t('fms.cost.vehicleId'), component: 'Input', show: false },
  { field: 'docType', label: t('fms.vehicles.docType'), component: 'Select', componentProps: { options: [{ label: t('fms.vehicles.regDoc'), value: 'registration' }, { label: t('fms.vehicles.insuranceDoc'), value: 'insurance' }, { label: t('fms.vehicles.inspectionDoc'), value: 'inspection' }] }, required: true },
  { field: 'docNumber', label: t('fms.vehicles.docNumber'), component: 'Input', required: true },
  { field: 'issueDate', label: t('fms.vehicles.issueDate'), component: 'DatePicker', componentProps: { format: 'YYYY-MM-DD', valueFormat: 'YYYY-MM-DD' } },
  { field: 'expiryDate', label: t('fms.vehicles.expiryDate'), component: 'DatePicker', componentProps: { format: 'YYYY-MM-DD', valueFormat: 'YYYY-MM-DD' } },
  { field: 'files', label: 'Tài liệu (Files)', slot: 'fileUpload' },
];
