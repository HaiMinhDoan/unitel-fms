import { h } from 'vue';
import { Tag, Image } from 'ant-design-vue';

export function handlePreview(att: any) {
  if (!att || !att.publicUrl) return;
  const ext = att.extension ? att.extension.replace('.', '').toLowerCase() : '';
  // const isOffice = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'].includes(ext);

  // if (isOffice) {
  //   // Attempt to use Microsoft Office Web Viewer
  //   const viewerUrl = `https://view.officeapps.live.com/op/view.aspx?src=${encodeURIComponent(att.publicUrl)}`;
  //   window.open(viewerUrl, '_blank');
  // } else {
    // Fallback for non-images and non-office files (like PDF)
    window.open(att.publicUrl, '_blank');
  // }
}

export function renderAttachments(attachments: any[]) {
  if (!attachments || !attachments.length) return '';
  return h(
    'div',
    { style: { display: 'flex', flexDirection: 'column', gap: '4px' } },
    attachments.map((att: any, index: number) => {
      const isImageMime = att.mimeType && att.mimeType.startsWith('image/');
      const ext = att.extension ? att.extension.replace('.', '').toLowerCase() : '';
      const isImageExt = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'].includes(ext);
      const isImageName = att.originalName && att.originalName.match(/\.(jpeg|jpg|gif|png|webp|svg)$/i);
      const isImage = isImageMime || isImageExt || isImageName;
      
      if (isImage) {
        return h(Image, {
          src: att.publicUrl,
          alt: att.originalName || `Image ${index + 1}`,
          style: { maxWidth: '80px', maxHeight: '80px', objectFit: 'cover', border: '1px solid #d9d9d9', borderRadius: '4px', cursor: 'pointer' },
          preview: true,
        });
      }

      return h(
        Tag,
        {
          color: 'green',
          style: { cursor: 'pointer', margin: 0, overflow: 'hidden', textOverflow: 'ellipsis', maxWidth: '150px' },
          onClick: () => handlePreview(att),
          title: att.originalName || `File ${index + 1}`
        },
        () => att.originalName || `File ${index + 1}`
      );
    })
  );
}
