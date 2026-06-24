import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.trips.assignment'), dataIndex: 'dispatchAssignmentId', width: 150 },
  { title: t('fms.common.status'), dataIndex: 'status', width: 120 },
  { title: t('fms.trips.plannedStart'), dataIndex: 'plannedStartAt', width: 180 },
  { title: t('fms.trips.plannedEnd'), dataIndex: 'plannedEndAt', width: 180 },
  { title: t('fms.trips.actualStart'), dataIndex: 'actualStartAt', width: 180 },
  { title: t('fms.trips.actualEnd'), dataIndex: 'actualEndAt', width: 180 },
  { title: t('fms.trips.distance'), dataIndex: 'totalDistanceKm', width: 150 },
  { title: t('fms.trips.slaRisk'), dataIndex: 'slaAtRisk', width: 100, customRender: ({ record }) => record.slaAtRisk ? 'Có' : 'Không' },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'dispatchAssignmentId', label: t('fms.trips.assignment'), component: 'Input', colProps: { span: 6 } },
];
