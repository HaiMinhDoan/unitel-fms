# Module 8 — File Attachment (dùng chung xuyên suốt)

Entity: `FileAttachment` (`file_attachments`)

**Nhóm cache**: KHÔNG

---

| # | API | Method | URL | Quyền | Logic | Workspace |
|---|---|---|---|---|---|---|
| 1 | Upload file | POST | `/api/v1/file-attachment/upload` | `ALL` | OR | Có |
| 2 | Lấy theo ID | GET | `/api/v1/file-attachment/get-by-id/{id}` | `ALL` | OR | Có |
| 3 | Lấy theo entity (vd: tất cả ảnh của 1 xe) | GET | `/api/v1/file-attachments/get-by-entity?entityType={}&entityId={}` | `ALL` | OR | Có |
| 4 | Lọc/phân trang | POST | `/api/v1/file-attachments/filter` | `SYSTEM_ADMIN` | — | Có |
| 5 | Tải file (download/presigned URL) | GET | `/api/v1/file-attachment/{id}/download` | `ALL` | OR | Có |
| 6 | Xoá mềm | DELETE | `/api/v1/file-attachment/soft-delete/{id}` | `ALL` (chỉ người upload hoặc admin) | OR | Có |
| 7 | Xoá cứng (xoá cả MinIO) | DELETE | `/api/v1/file-attachment/hard-delete/{id}` | `SYSTEM_ADMIN` | — | Không |

### Chi tiết logic nghiệp vụ đặc thù

**`upload`** — validate `FileType.fromMimeType()` (enum đã có sẵn trong code) trước khi đẩy lên MinIO, set `bucket`/`object_key` theo convention `{entityType}/{entityId}/{uuid}.{ext}`:

```java
@Transactional
public FileAttachment upload(MultipartFile file, String entityType, UUID entityId) {
    FileType type = FileType.fromMimeType(file.getContentType()); // throw IllegalArgumentException nếu không hỗ trợ

    String objectKey = String.format("%s/%s/%s.%s", entityType, entityId,
            UUID.randomUUID(), getExtension(file.getOriginalFilename()));

    minioClient.putObject(PutObjectArgs.builder()
            .bucket(ConstantVariables.MINIO_BUCKET_NAME)
            .object(objectKey)
            .stream(file.getInputStream(), file.getSize(), -1)
            .contentType(file.getContentType())
            .build());

    FileAttachment attachment = FileAttachment.builder()
            .bucket(ConstantVariables.MINIO_BUCKET_NAME)
            .objectKey(objectKey)
            .originalName(file.getOriginalFilename())
            .mimeType(file.getContentType())
            .extension(getExtension(file.getOriginalFilename()))
            .sizeBytes(file.getSize())
            .entityType(entityType)
            .entityId(entityId)
            .uploadedBy(SecurityContextHolder.getAuthInfo().getUserId())
            .status("active")
            .build();

    return repository.save(attachment);
}
```

**`download`** — KHÔNG proxy file qua backend (tốn băng thông server không cần thiết với file lớn — tài liệu đặc tả nói rõ multipart tới 500MB). Thay vào đó trả về **presigned URL** có TTL ngắn (ví dụ 15 phút), client tải trực tiếp từ MinIO:

```java
@GetMapping("/{id}/download")
@RequireAuth(roles = {RoleType.ALL}, inWorkspace = true)
public ResponseData<String> getDownloadUrl(@PathVariable UUID id) {
    FileAttachment attachment = fileAttachmentService.getOne(id)
            .orElseThrow(() -> new NotFoundException("File không tồn tại"));

    String presignedUrl = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(attachment.getBucket())
                    .object(attachment.getObjectKey())
                    .expiry(15, TimeUnit.MINUTES)
                    .build());

    return ResponseData.<String>builder().status(200).data(presignedUrl).message("Thành công").build();
}
```

**`hardDelete` / `deleteAllByEntity`** — helper dùng nội bộ bởi các module khác (ví dụ `VehicleDocumentService.hardDelete` ở Module 2 đã gọi tới):

```java
@Transactional
public void deleteAllByEntity(String entityType, UUID entityId) {
    List<FileAttachment> files = repository.findByEntityTypeAndEntityId(entityType, entityId);
    for (FileAttachment file : files) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(file.getBucket()).object(file.getObjectKey()).build());
    }
    repository.deleteAll(files);
}
```

**Soft-delete với check ownership** — tương tự `Notification`/`User`, không phải ai cũng xoá được file của người khác:

```java
@Override
@Transactional
public void softDelete(UUID id) {
    FileAttachment file = getOne(id).orElseThrow(() -> new NotFoundException("File không tồn tại"));
    AuthInfo authInfo = SecurityContextHolder.getAuthInfo();

    boolean isOwner = file.getUploadedBy().equals(authInfo.getUserId());
    boolean isAdmin = authInfo.hasAnyRole(RoleType.SYSTEM_ADMIN);
    if (!isOwner && !isAdmin) {
        throw new AccessDeniedException("Chỉ người upload hoặc SYSTEM_ADMIN mới được xoá file này");
    }
    changeStatus(id, "deleted");
}
```