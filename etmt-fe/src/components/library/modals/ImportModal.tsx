import { InboxOutlined } from "@ant-design/icons";
import { Button, message, Modal, notification, Upload } from "antd";
import { FunctionComponent, useState } from "react";
import { API_REQUEST_TEST_TIMEOUT_MS } from "../../../common/APIUtils";
import { ERR_MSG, SUCCESS_MSG } from "../../../common/Common";
import { API_BASE_URL } from "../../../Constants";
import { useAuth } from "../../auth/Auth";
import { ItemModalProps } from "../items/ItemInfo";

const { Dragger } = Upload;

const ImportModal: FunctionComponent<ItemModalProps> = (props: ItemModalProps) => {
  const auth = useAuth();
  const [confirmLoading, setConfirmLoading] = useState(false);

  const handleOk = () => {
    props.modal.setModalState({ isVisible: false, isEdit: false });
    props.onModalClose();
  }

  const handleCancel = () => {
    props.modal.setModalState({ isVisible: false, isEdit: false });
  }

  const uploadProps = {
    name: 'file',
    multiple: false,
    action: API_BASE_URL + "/categories/import/" + props.selectedItemData?.id,
    headers: {
      "Authorization": 'Bearer ' + auth.user.token
    },
    onChange(info: any) {
      setConfirmLoading(true);
      setTimeout(() => {
        const { status } = info.file;
        if (status !== 'uploading') {
          console.log(info.file, info.fileList);
          setConfirmLoading(false);
        }
        if (status === 'done') {
          notification.success({
            message: SUCCESS_MSG,
            description: 'Data imported!'
          });
          setConfirmLoading(false);
        } else if (status === 'error') {
          notification.error({
            message: ERR_MSG,
            description: 'There was an error while importing data... Please try again.'
          })
          setConfirmLoading(false);
          message.error(`${info.file.name} file upload failed.`);
        }
      }, API_REQUEST_TEST_TIMEOUT_MS);
    },
    onDrop(e: any) {
      console.log('Dropped files', e.dataTransfer.files);
    },
  };

  return (
    <Modal
      title={"Import category from XML under: " + props.selectedItemData?.title}
      visible={props.modal.modalState.isVisible}
      confirmLoading={confirmLoading}
      onCancel={handleCancel}
      footer={[
        <Button type="primary" loading={confirmLoading} onClick={handleOk}>
          Finish
        </Button>
      ]}
      destroyOnClose={true}
    >
      <Dragger {...uploadProps}>
        <p className="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p className="ant-upload-text">Click or drag a file to this area to upload</p>
        <p className="ant-upload-hint">
          Supported files are in the Moodle XML format
        </p>
      </Dragger>
    </Modal>
  );
}

export default ImportModal;
