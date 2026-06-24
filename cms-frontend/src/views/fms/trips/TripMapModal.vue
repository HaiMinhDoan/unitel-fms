<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="Hành trình Chuyến đi" width="900px">
    <div id="trip-map" style="height: 500px; width: 100%;"></div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { nextTick, ref } from 'vue';
  import { BasicModal, useModalInner } from '@/components/Modal';
  import L from 'leaflet';
  import 'leaflet/dist/leaflet.css';
  import { filterTripStops } from '@/api/fms/trip';

  const map = ref<L.Map | null>(null);
  const routeLayer = ref<L.LayerGroup | null>(null);

  const [registerModal] = useModalInner(async (data) => {
    const tripId = data.record.id;
    
    // Fetch stops
    const res = await filterTripStops({
      page: 0,
      size: 100,
      filters: [{ fieldName: 'trip.id', operation: 'EQUALS', value: tripId }]
    });
    
    const stops = res.content || [];

    await nextTick();
    
    setTimeout(() => {
      if (map.value) map.value.invalidateSize();
    }, 200);

    if (!map.value) {
      map.value = L.map('trip-map').setView([21.028511, 105.804817], 13); // Default Hanoi
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
      }).addTo(map.value as any);
    }
    
    if (routeLayer.value) {
      map.value.removeLayer(routeLayer.value as any);
    }
    
    routeLayer.value = L.layerGroup().addTo(map.value as any);
    
    const latlngs: L.LatLngExpression[] = [];
    
    stops.forEach(stop => {
      if (stop.lat && stop.lng) {
        latlngs.push([stop.lat, stop.lng]);
        L.marker([stop.lat, stop.lng])
          .bindPopup(`<b>${stop.locationName || 'Trạm'}</b><br/>Sequence: ${stop.stopSequence}`)
          .addTo(routeLayer.value as any);
      }
    });
    
    if (latlngs.length > 1) {
      const polyline = L.polyline(latlngs, { color: 'blue', weight: 4 }).addTo(routeLayer.value as any);
      map.value.fitBounds(polyline.getBounds());
    } else if (latlngs.length === 1) {
      map.value.setView(latlngs[0], 14);
    }
  });
</script>
