import { Button, Checkbox, Modal, notification, Typography } from "antd";
import { FunctionComponent, useState } from "react";
import { apiDeleteItem } from "../../../common/APIUtils";
import { ERR_MSG, ItemType, SUCCESS_MSG } from "../../../common/Common";
import { useAuth } from "../../auth/Auth";
import { ItemModalProps } from "../items/ItemInfo";

const Text = Typography;

const RemoveItemModal: FunctionComponent<ItemModalProps> = (props: ItemModalProps) => {
  const auth = useAuth();
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [deleteChildrenInputValue, setDeleteChildrenInputValue] = useState(false);

  const handleOk = () => {
    console.log(`remove ${props.selectedItemData?.title}`);

    setConfirmLoading(true);

    apiDeleteItem(props.selectedItemData?.id, deleteChildrenInputValue, auth.user?.token)
      .then(() => {
        notification.success({
          message: SUCCESS_MSG,
          description: 'Item removed!'
        });
        props.modal.setModalState({ isVisible: false, isEdit: false });
        props.onModalClose();
      }).catch(error => {
        console.error(error);
        notification.error({
          message: ERR_MSG,
          description: 'There was an error while removing the item... Please try again.'
        })
      }).finally(() => setConfirmLoading(false))
  }

  const handleCancel = () => {
    props.modal.setModalState({ isVisible: false, isEdit: false });
  };

  return (
    <Modal
      title={"Remove: " + props.selectedItemData?.title}
      visible={props.modal.modalState.isVisible}
      confirmLoading={confirmLoading}
      onCancel={handleCancel}
      footer={[
        <Button type="primary" loading={confirmLoading} danger onClick={handleOk}>
          Delete
        </Button>
      ]}
    >
      <Text>Are you sure you want to delete '{props.selectedItemData?.title}'?</Text>
      {
        props.selectedItemData?.type === ItemType.category
          ?
          <>
            <Checkbox onChange={(event) => setDeleteChildrenInputValue(event.target.checked)} style={{ margin: "15px 0" }}>
              <p className="error">Delete children</p>
            </Checkbox>
            <i>(all items under this category will be deleted aswell)</i>
          </>
          :
          <></>
      }
    </Modal>
  );
}

export default RemoveItemModal;
