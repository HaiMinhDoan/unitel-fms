<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
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
            Tải lên hóa đơn
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
  import { formSchema } from './cost.data';
  import { createFuelLog } from '@/api/fms/cost';
  import { Upload as AUpload, Button as AButton } from 'ant-design-vue';

  const emit = defineEmits(['success', 'register']);
  const fileList = ref<any[]>([]);
  const isUpdate = ref(true);
  const rowId = ref('');

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 140,
    baseColProps: { span: 24 },
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetFields();
    fileList.value = [];
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;

    if (unref(isUpdate)) {
      rowId.value = data.record.id;
      setFieldsValue({
        ...data.record,
      });
    }
  });

  const getTitle = computed(() => (!unref(isUpdate) ? 'Thêm Nhật ký đổ nhiên liệu' : 'Sửa Nhật ký đổ nhiên liệu'));

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

      await createFuelLog(params);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
