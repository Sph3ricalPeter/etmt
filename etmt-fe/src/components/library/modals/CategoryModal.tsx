import { Button, Form, Input, Modal, notification } from "antd";
import { FunctionComponent, useEffect, useState } from "react";
import { apiPostItem } from "../../../common/APIUtils";
import { Category, ERR_MSG, ItemType, SUCCESS_MSG } from "../../../common/Common";
import { useAuth } from "../../auth/Auth";
import { ItemModalProps } from "../items/ItemInfo";

const CategoryModal: FunctionComponent<ItemModalProps> = (props: ItemModalProps) => {
  const auth = useAuth();
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [initialValues, setInitialValues] = useState<Category>({} as Category);

  useEffect(() => {
    if (props.modal.modalState.isVisible) {
      setInitialValues(props.modal.modalState.isEdit ? props.selectedItemData : {});
    }
  }, [props.modal.modalState.isVisible])

  const handleOk = (formValues: Category) => {
    var item: Category = formValues;
    item.type = ItemType.category;

    if (props.modal.modalState.isEdit) {
      item.id = initialValues.id;
      item.parentId = initialValues.parentId;
    } else {
      // if in add mode, parent category is selected
      item.parentId = props.selectedItemData?.id;
    }

    console.log(`post category (${props.modal.modalState.isEdit ? "edit" : "add"}): `, item);

    setConfirmLoading(true);

    apiPostItem(item, auth.user?.token)
      .then(() => {
        notification.success({
          message: SUCCESS_MSG,
          description: props.modal.modalState.isEdit ? 'Category edited!' : 'Category added!'
        });
        props.modal.setModalState({ isVisible: false, isEdit: false });
        props.onModalClose();
      }).catch(error => {
        console.error(error);
        notification.error({
          message: ERR_MSG,
          description: 'There was an error while posting a category... Please try again.'
        })
      }).finally(() => setConfirmLoading(false))
  };

  const handleCancel = () => {
    props.modal.setModalState({ isVisible: false, isEdit: false });
  };

  const handleSubmitFailed = (errorInfo: any) => {
    console.log("submit failed: ", errorInfo);
  }

  return (
    <Modal
      title={
        props.modal.modalState.isEdit ?
          "Edit category " + props.selectedItemData?.title :
          "Add subcategory under category " + props.selectedItemData?.title
      }
      visible={props.modal.modalState.isVisible}
      confirmLoading={confirmLoading}
      onCancel={handleCancel}
      destroyOnClose={true}
      footer={[
        <Button type="primary" form="addCategoryForm" key="submit" htmlType="submit" loading={confirmLoading}>
          Submit
        </Button>
      ]}
    >
      <Form
        name="addCategoryForm"
        labelCol={{ span: 4 }}
        layout="horizontal"
        initialValues={initialValues}
        onFinish={handleOk}
        onFinishFailed={handleSubmitFailed}
      >

        <Form.Item
          label="Title"
          name="title"
          rules={[{ required: true, message: 'Title cannot be empty!' }]}
        >
          <Input autoFocus/>
        </Form.Item>

      </Form>
    </Modal>
  );
}

export default CategoryModal;
