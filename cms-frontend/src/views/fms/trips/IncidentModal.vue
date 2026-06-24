<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="Báo cáo Sự cố Chuyến đi" @ok="handleSubmit">
    <BasicForm @register="registerForm">
      <template #fileUpload>
        <a-upload
          v-model:file-list="fileList"
          :before-upload="beforeUpload"
          :max-count="5"
          multiple
          accept="image/*"
        >
          <a-button>
            Tải lên hình ảnh/tài liệu
          </a-button>
        </a-upload>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '@/components/Modal';
  import { BasicForm, useForm } from '@/components/Form/index';
  import { incidentFormSchema } from './incident.data';
  import { createTripIncident } from '@/api/fms/trip';
  import { Upload as AUpload, Button as AButton } from 'ant-design-vue';

  const emit = defineEmits(['success', 'register']);
  const currentTripId = ref('');
  const fileList = ref<any[]>([]);

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 140,
    baseColProps: { span: 24 },
    schemas: incidentFormSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    fileList.value = [];
    setModalProps({ confirmLoading: false });
    currentTripId.value = data?.tripId;
    
    setFieldsValue({
      tripId: currentTripId.value,
    });
  });

  const beforeUpload = (file: File) => {
    return false; // Prevent automatic upload
  };

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      
      const files = fileList.value.map(f => f.originFileObj || f);
      const params = {
        ...values,
        files: files.length > 0 ? files : undefined,
      };

      await createTripIncident(params);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
