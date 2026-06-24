<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="Bản đồ Phương tiện" width="800px">
    <div id="vehicle-map" style="height: 500px; width: 100%;"></div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { nextTick, ref } from 'vue';
  import { BasicModal, useModalInner } from '@/components/Modal';
  import L from 'leaflet';
  import 'leaflet/dist/leaflet.css';

  const map = ref<L.Map | null>(null);
  const marker = ref<L.Marker | null>(null);

  const [registerModal] = useModalInner(async (data) => {
    // Default to Hanoi if no coordinates
    const lat = data.record.lastKnownLat || 21.028511; 
    const lng = data.record.lastKnownLng || 105.804817;

    await nextTick();
    
    // Invalidate size in case modal animation affects bounds
    setTimeout(() => {
      if (map.value) map.value.invalidateSize();
    }, 200);

    if (!map.value) {
      map.value = L.map('vehicle-map').setView([lat, lng], 13);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
      }).addTo(map.value as any);
    } else {
      map.value.setView([lat, lng], 13);
    }

    if (marker.value) {
      marker.value.setLatLng([lat, lng]);
    } else {
      marker.value = L.marker([lat, lng]).addTo(map.value as any);
    }
  });
</script>
