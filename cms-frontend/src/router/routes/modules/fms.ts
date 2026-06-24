import type { AppRouteModule } from '@/router/types';

import { LAYOUT } from '@/router/constant';

import { t } from '@/hooks/web/useI18n';

const fms: AppRouteModule = {
  path: '/fms',
  name: 'Fms',
  component: LAYOUT,
  redirect: '/fms/users',
  meta: {
    orderNo: 20,
    icon: 'ion:car-outline',
    title: t('routes.fms.title'),
  },
  children: [
    {
      path: 'users',
      name: 'UserManagement',
      component: () => import('@/views/fms/users/index.vue'),
      meta: { title: t('routes.fms.users') },
    },
    {
      path: 'organizations',
      name: 'OrganizationManagement',
      component: () => import('@/views/fms/organizations/index.vue'),
      meta: { title: t('routes.fms.organizations') },
    },
    {
      path: 'customers',
      name: 'CustomerManagement',
      component: () => import('@/views/fms/customers/index.vue'),
      meta: { title: t('routes.fms.customers') },
    },
    {
      path: 'partners',
      name: 'PartnerManagement',
      component: () => import('@/views/fms/partners/index.vue'),
      meta: { title: t('routes.fms.partners') },
    },
    {
      path: 'partner-vehicles',
      name: 'PartnerVehicles',
      component: () => import('@/views/fms/partner-vehicles/index.vue'),
      meta: { title: t('routes.fms.partnerVehicles') },
    },
    {
      path: 'partner-drivers',
      name: 'PartnerDrivers',
      component: () => import('@/views/fms/partner-drivers/index.vue'),
      meta: { title: t('routes.fms.partnerDrivers') },
    },
    {
      path: 'vehicles',
      name: 'VehicleManagement',
      component: () => import('@/views/fms/vehicles/index.vue'),
      meta: { title: t('routes.fms.vehicles') },
    },
    {
      path: 'vehicle-types',
      name: 'VehicleTypeManagement',
      component: () => import('@/views/fms/vehicle-types/index.vue'),
      meta: { title: t('routes.fms.vehicleTypes') },
    },
    {
      path: 'drivers',
      name: 'DriverManagement',
      component: () => import('@/views/fms/drivers/index.vue'),
      meta: { title: t('routes.fms.drivers') },
    },
    {
      path: 'dispatch',
      name: 'DispatchManagement',
      component: () => import('@/views/fms/dispatch/index.vue'),
      meta: { title: t('routes.fms.dispatch') },
    },
    {
      path: 'dispatch-assignments',
      name: 'DispatchAssignments',
      component: () => import('@/views/fms/dispatch-assignments/index.vue'),
      meta: { title: t('routes.fms.dispatchAssignments') },
    },
    {
      path: 'trips',
      name: 'TripTracking',
      component: () => import('@/views/fms/trips/index.vue'),
      meta: { title: t('routes.fms.trips') },
    },
    {
      path: 'cost',
      name: 'CostManagement',
      component: () => import('@/views/fms/cost/index.vue'),
      meta: { title: t('routes.fms.cost') },
    },
    {
      path: 'maintenance-orders',
      name: 'MaintenanceOrders',
      component: () => import('@/views/fms/maintenance-orders/index.vue'),
      meta: { title: t('routes.fms.maintenanceOrders') },
    },
  ],
};

export default fms;
