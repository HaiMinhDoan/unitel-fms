import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';
import { renderAttachments } from '@/utils/filePreview';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.cost.vehicleId'), dataIndex: 'vehicleId', width: 120 },
  { title: t('fms.cost.fuelType'), dataIndex: 'fuelType', width: 120 },
  { title: t('fms.cost.volumeLiters'), dataIndex: 'volumeLiters', width: 120 },
  { title: t('fms.cost.unitPrice'), dataIndex: 'unitPrice', width: 120 },
  { title: t('fms.maintenance.cost'), dataIndex: 'totalCost', width: 150 },
  { title: t('fms.cost.location'), dataIndex: 'location', width: 180 },
  { title: t('fms.cost.filledAt'), dataIndex: 'filledAt', width: 180 },
  { title: 'Đính kèm', dataIndex: 'attachments', width: 200, customRender: ({ record }) => renderAttachments(record.attachments) },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'vehicleId', label: t('fms.cost.vehicleId'), component: 'Input', colProps: { span: 6 } },
];

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'vehicleId', label: t('fms.cost.vehicleId'), component: 'Input', required: true },
  { field: 'driverId', label: t('fms.cost.driverId'), component: 'Input', required: true },
  { field: 'fuelType', label: t('fms.cost.fuelType'), component: 'Select', componentProps: { options: [{ label: t('fms.cost.gasoline'), value: 'petrol' }, { label: t('fms.cost.diesel'), value: 'diesel' }] } },
  { field: 'volumeLiters', label: t('fms.cost.volumeLiters'), component: 'InputNumber' },
  { field: 'totalCost', label: t('fms.maintenance.cost'), component: 'InputNumber' },
  { field: 'odometerReading', label: t('fms.cost.currentOdometer'), component: 'InputNumber' },
  { field: 'location', label: t('fms.cost.location'), component: 'Input' },
  { field: 'filledAt', label: t('fms.cost.filledAt'), component: 'DatePicker', componentProps: { showTime: true, format: 'YYYY-MM-DD HH:mm:ss' } },
  { field: 'reportedConsumption', label: 'Tiêu thụ (Báo cáo)', component: 'InputNumber' },
  { field: 'telemetryConsumption', label: 'Tiêu thụ (Đo lường)', component: 'InputNumber' },
  { field: 'anomalyFlagged', label: 'Có bất thường', component: 'Switch' },
  { field: 'anomalyNotes', label: 'Ghi chú bất thường', component: 'InputTextArea' },
  { field: 'files', label: 'Hóa đơn/Chứng từ', slot: 'fileUpload' },
];
