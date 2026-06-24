<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <BasicForm @register="registerForm">
      <template #fileUpload>
        <a-upload
          v-model:file-list="fileList"
          :before-upload="beforeUpload"
          :max-count="5"
          multiple
        >
          <a-button>
            Tải lên tài liệu
          </a-button>
        </a-upload>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '@/components/Modal';
  import { BasicForm, useForm } from '@/components/Form/index';
  import { documentFormSchema } from './document.data';
  import { createVehicleDocument, updateVehicleDocument } from '@/api/fms/vehicle';
  import { Upload as AUpload, Button as AButton } from 'ant-design-vue';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);
  const currentId = ref('');
  const currentVehicleId = ref('');
  const fileList = ref<any[]>([]);

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 140,
    baseColProps: { span: 24 },
    schemas: documentFormSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    fileList.value = [];
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    currentVehicleId.value = data?.vehicleId || '';

    if (unref(isUpdate)) {
      currentId.value = data.record.id;
      // If there are existing files, we might want to display them here in fileList
      setFieldsValue({
        ...data.record,
      });
    } else {
      currentId.value = '';
      setFieldsValue({
        vehicleId: currentVehicleId.value,
      });
    }
  });

  const getTitle = computed(() => (!unref(isUpdate) ? 'Thêm tài liệu' : 'Sửa tài liệu'));

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

      if (unref(isUpdate)) {
        await updateVehicleDocument(currentId.value, params);
      } else {
        await createVehicleDocument(params);
      }
      
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
