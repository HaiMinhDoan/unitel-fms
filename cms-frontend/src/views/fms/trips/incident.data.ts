import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

import { renderAttachments } from '@/utils/filePreview';

export const incidentColumns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.trips.incidentType'), dataIndex: 'incidentType', width: 150 },
  { title: t('fms.trips.severity'), dataIndex: 'severity', width: 100 },
  { title: t('fms.trips.description'), dataIndex: 'description', width: 250 },
  { title: t('fms.trips.reportedAt'), dataIndex: 'reportedAt', width: 150 },
  { title: t('fms.common.status'), dataIndex: 'status', width: 100 },
  { title: 'Đính kèm', dataIndex: 'attachments', width: 200, customRender: ({ record }) => renderAttachments(record.attachments) },
];

export const incidentFormSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'tripId', label: t('fms.trips.tripId'), component: 'Input', show: false },
  { field: 'incidentType', label: t('fms.trips.incidentType'), component: 'Select', componentProps: { options: [{ label: 'Tai nạn', value: 'accident' }, { label: 'Hỏng hóc', value: 'breakdown' }, { label: 'Chậm trễ', value: 'delay' }, { label: 'Khác', value: 'other' }] }, required: true },
  { field: 'severity', label: t('fms.trips.severity'), component: 'Select', componentProps: { options: [{ label: 'Thấp', value: 'low' }, { label: 'Trung bình', value: 'medium' }, { label: 'Cao', value: 'high' }, { label: 'Nghiêm trọng', value: 'critical' }] }, required: true },
  { field: 'description', label: t('fms.trips.description'), component: 'InputTextArea', required: true },
  { field: 'reportedAt', label: t('fms.trips.reportedAt'), component: 'DatePicker', componentProps: { showTime: true, format: 'YYYY-MM-DD HH:mm:ss' } },
  { field: 'files', label: 'Hình ảnh/Tài liệu (Files)', slot: 'fileUpload' },
];
