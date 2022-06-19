import { Button, DatePicker, Form, Input, Modal, notification } from "antd";
import TextArea from "antd/lib/input/TextArea";
import moment from "moment";
import { FunctionComponent, useEffect, useState } from "react";
import { apiPostItem } from "../../../common/APIUtils";
import { ERR_MSG, SUCCESS_MSG, Variant } from "../../../common/Common";
import { useAuth } from "../../auth/Auth";
import { ItemModalProps } from "../items/ItemInfo";

const VariantModal: FunctionComponent<ItemModalProps> = (props: ItemModalProps) => {
  const auth = useAuth();
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [initialValues, setInitialValues] = useState<Variant>({} as Variant);

  useEffect(() => {
    if (props.modal.modalState.isVisible) {
      if (props.modal.modalState.isEdit) {
        // date needs to be transformed to moment before date picker can load it
        let initData = Object.assign({}, props.selectedItemData);
        initData.date = props.selectedItemData.date ? moment(props.selectedItemData.date) : undefined;
        setInitialValues(initData);
      } else {
        setInitialValues({} as Variant);
      }
    }
  }, [props.modal.modalState.isVisible])

  const handleOk = (formValues: Variant) => {
    // take the fetched item
    var item: Variant = props.selectedItemData;

    // change only the fields that are in the form
    item.title = formValues.title;
    item.date = formValues.date;
    item.instructions = formValues.instructions;

    console.log(`post variant (${props.modal.modalState.isEdit ? "edit" : "add"}): `, item);

    setConfirmLoading(true);

    apiPostItem(item, auth.user?.token)
      .then(() => {
        notification.success({
          message: SUCCESS_MSG,
          description: initialValues ? 'Variant edited!' : 'Variant added!'
        });
        props.modal.setModalState({ isVisible: false, isEdit: false });
        props.onModalClose();
      }).catch(error => {
        console.error(error);
        notification.error({
          message: ERR_MSG,
          description: 'There was an error while posting a test variant... Please try again.'
        })
      }).finally(() => setConfirmLoading(false))
  };

  const handleCancel = () => {
    props.modal.setModalState({ isVisible: false, isEdit: false });
  };

  const handleSubmitFailed = (errorInfo: any) => {
    console.log("submit failed: ", errorInfo);
  }

  const onDatePickerValueChanged = (value: any) => {
    console.log(value);
  }

  return (
    <Modal
      title={
        props.modal.modalState.isEdit ?
          "Edit variant: " + props.selectedItemData?.title :
          "Add variant under category: " + props.selectedItemData?.title
      }
      visible={props.modal.modalState.isVisible}
      confirmLoading={confirmLoading}
      onCancel={handleCancel}
      destroyOnClose={true}
      footer={[
        <Button type="primary" form="variantForm" key="submit" htmlType="submit" loading={confirmLoading}>
          Submit
        </Button>
      ]}
    >
      <Form
        name="variantForm"
        labelCol={{ span: 5 }}
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

        <Form.Item
          label="Test date"
          name="date"
          rules={[{ required: true, message: 'Date cannot be empty!' }]}
        >
          <DatePicker
            format="YYYY-MM-DD HH:mm:ss"
            showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss'), minuteStep: 10, showSecond: false }}
            onChange={onDatePickerValueChanged}
          />
        </Form.Item>

        <Form.Item
          label="Instructions"
          name="instructions"
        >
          <TextArea style={{ minHeight: "100px" }} />
        </Form.Item>

      </Form>
    </Modal>
  );
}

export default VariantModal;
