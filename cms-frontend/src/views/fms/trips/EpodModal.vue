<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="Nộp chứng từ EPOD" @ok="handleSubmit">
    <BasicForm @register="registerForm">
      <template #photoUpload>
        <a-upload
          v-model:file-list="photoList"
          :before-upload="beforeUpload"
          :max-count="1"
          accept="image/*"
        >
          <a-button>
            Chụp/Tải lên ảnh hàng hóa
          </a-button>
        </a-upload>
      </template>
      <template #signatureUpload>
        <a-upload
          v-model:file-list="signatureList"
          :before-upload="beforeUpload"
          :max-count="1"
          accept="image/*"
        >
          <a-button>
            Tải lên chữ ký
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
  import { submitTripStopEpod } from '@/api/fms/trip';
  import { Upload as AUpload, Button as AButton } from 'ant-design-vue';

  const emit = defineEmits(['success', 'register']);
  const currentTripStopId = ref('');
  const photoList = ref<any[]>([]);
  const signatureList = ref<any[]>([]);

  const [registerForm, { resetFields, validate }] = useForm({
    labelWidth: 140,
    baseColProps: { span: 24 },
    schemas: [
      { field: 'photo', label: 'Ảnh hàng hóa', slot: 'photoUpload' },
      { field: 'signature', label: 'Chữ ký khách hàng', slot: 'signatureUpload' },
    ],
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    photoList.value = [];
    signatureList.value = [];
    setModalProps({ confirmLoading: false });
    currentTripStopId.value = data?.tripStopId;
  });

  const beforeUpload = (file: File) => {
    return false; // Prevent automatic upload
  };

  async function handleSubmit() {
    try {
      await validate();
      setModalProps({ confirmLoading: true });
      
      const photoFile = photoList.value.length > 0 ? (photoList.value[0].originFileObj || photoList.value[0]) : undefined;
      const signatureFile = signatureList.value.length > 0 ? (signatureList.value[0].originFileObj || signatureList.value[0]) : undefined;

      const params = {
        photo: photoFile,
        signature: signatureFile,
      };

      await submitTripStopEpod(currentTripStopId.value, params);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
