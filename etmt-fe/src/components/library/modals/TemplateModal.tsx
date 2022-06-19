import { DeleteOutlined, InfoCircleOutlined, PlusOutlined } from "@ant-design/icons";
import { Button, Cascader, Col, Divider, Form, Input, InputNumber, Modal, notification, Row, Tooltip } from "antd";
import { FunctionComponent, useEffect, useState } from "react";
import { apiPostItem } from "../../../common/APIUtils";
import { ERR_MSG, ItemType, SUCCESS_MSG, Template } from "../../../common/Common";
import { useAuth } from "../../auth/Auth";
import { ItemModalProps } from "../items/ItemInfo";

const formItemLayoutWithOutLabel = {
  wrapperCol: {
    xs: { span: 24, offset: 0 },
    sm: { span: 20, offset: 4 },
  },
};

interface CascaderNode {
  value: string,
  label: string,
  children: Array<CascaderNode>
}

interface TemplateModalProps extends ItemModalProps {
  parentItemData: any
}

const TemplateModal: FunctionComponent<TemplateModalProps> = (props: TemplateModalProps) => {
  const auth = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [initialValues, setInitialValues] = useState<Template>({} as Template);

  const [categories, setCategories] = useState<Array<CascaderNode>>();

  useEffect(() => {
    if (props.modal.modalState.isVisible) {
      console.log(props.selectedItemData);

      var parentCategory: any;
      if (props.modal.modalState.isEdit) {
        parentCategory = transformDataRecursive(props.parentItemData);
      } else {
        // when adding, the parent category is selected
        parentCategory = transformDataRecursive(props.selectedItemData);
      }
      // we want to only look at the children of that category
      setCategories(parentCategory.children);
    }

    var initData = {} as any;
    if (props.modal.modalState.isEdit) {
      Object.assign(initData, props.selectedItemData);
      initData.topics = initData.topics.map((topic: any) => {
        return {
          questionCount: topic.questionCount,
          points: topic.points,
          categories: topic.categories.map((row: any) =>
            row.map((category: any) => JSON.stringify({ title: category.title, id: category.id })))
        }
      });
      console.log("init categories: ", initData.topics[0]?.categories);
    }

    setInitialValues(initData as Template);

  }, [props.modal.modalState.isVisible])

  const transformDataRecursive = (root: any): CascaderNode => {
    var transformedRoot: CascaderNode = {
      value: JSON.stringify({ title: root.title, id: root.id }),
      label: root.title,
      children: []
    };

    root.children?.forEach((ch: any) => {
      if (ch.type === ItemType.category) {
        transformedRoot.children.push(transformDataRecursive(ch))
      }
    })

    return transformedRoot;
  }

  const handleOk = (formValues: any) => {
    formValues.topics = formValues.topics.map((topic: any) => {
      return {
        questionCount: topic.questionCount,
        points: topic.points,
        categories: topic.categories?.map((row: any) =>
          row.map((category: any) =>
            JSON.parse(category)))
      }
    });


    var item: Template = formValues;
    item.type = ItemType.template;


    if (props.modal.modalState.isEdit) {
      item.id = initialValues.id;
      item.parentId = initialValues.parentId;
    } else {
      // if in add mode, parent category is selected
      item.parentId = props.selectedItemData?.id;
    }

    console.log(`post template (${props.modal.modalState.isEdit ? "edit" : "add"}): `, item);

    setIsLoading(true);

    apiPostItem(item, auth.user?.token)
      .then(() => {
        notification.success({
          message: SUCCESS_MSG,
          description: props.modal.modalState.isEdit ? 'Template edited!' : 'Template added!'
        });
        props.modal.setModalState({ isVisible: false, isEdit: false });
        props.onModalClose();
      }).catch(error => {
        console.error(error);
        notification.error({
          message: ERR_MSG,
          description: 'There was an error while posting the template... Please try again.'
        })
      }).finally(() => setIsLoading(false))
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
          "Edit test template: " + props.selectedItemData?.title :
          "Add test template under category: " + props.selectedItemData?.title
      }
      visible={props.modal.modalState.isVisible}
      confirmLoading={isLoading}
      onCancel={handleCancel}
      destroyOnClose={true}
      footer={[
        <Button type="primary" form="addTemplateForm" key="submit" htmlType="submit" loading={isLoading}>
          Submit
        </Button>
      ]}
    >
      <p className="infoMsg">
        <InfoCircleOutlined /> Test template is a list of topics. Each topic defines how many questions with given amount of points will be present the generated test variants. A valid test template needs to have at least 1 topic
      </p>
      <Divider />
      <Form
        name="addTemplateForm"
        {...formItemLayoutWithOutLabel}
        labelCol={{ span: 5 }}
        layout="horizontal"
        onFinish={handleOk}
        onFinishFailed={handleSubmitFailed}
        initialValues={initialValues}
      >

        <Form.Item
          label="Template title"
          wrapperCol={{ flex: "auto", offset: 1 }}
          name="title"
          rules={[{ required: true, message: 'Title cannot be empty!' }]}
        >
          <Input autoFocus />
        </Form.Item>

        <Form.List name="topics" rules={[
          {
            validator: async (_, topics) => {
              if (topics === null || topics.length < 1) {
                return Promise.reject(new Error('At least 1 topic is required!'));
              }
            }
          }
        ]}>
          {(fields, { add, remove }, { errors }) => (
            <>
              {fields.map(({ key, name, ...restField }, index) => (
                <>
                  <Divider orientation="left" orientationMargin="0px" style={{ fontSize: "16px" }}>
                    Topic #{index + 1}
                  </Divider>
                  <Row>
                    <Col flex={1}>
                      <Form.Item
                        label="Categories"
                        labelCol={{ span: 4 }}
                        wrapperCol={{ flex: 1, offset: 2 }}
                        {...restField}
                        name={[name, 'categories']}
                      >
                        <Cascader
                          options={categories}
                          placeholder="Please select"
                          multiple onChange={(value, selectedOptions) => {
                            console.log("value: ", value)
                            console.log("selected options: ", selectedOptions)
                          }}
                        />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row wrap={false}>
                    <Col flex={2} style={{ minHeight: "30px", color: "gray" }}>
                      <Form.Item
                        label="Question count"
                        labelCol={{ span: 11 }}
                        wrapperCol={{ flex: "100px", offset: 2 }}
                        {...restField}
                        name={[name, 'questionCount']}
                        rules={[{ required: true, message: 'Missing' }]}
                      >
                        <InputNumber min={1} />
                      </Form.Item>
                    </Col>
                    <Col flex={1}></Col>
                    <Col flex={2}>
                      <Form.Item
                        label="Points"
                        labelCol={{ span: 7 }}
                        wrapperCol={{ flex: "100px", offset: 2 }}
                        {...restField}
                        name={[name, 'points']}
                      >
                        <InputNumber min={1} />
                      </Form.Item>
                    </Col>
                    <Col flex="auto">
                      <Tooltip title="Remove topic">
                        <Button type="primary" shape="circle" icon={<DeleteOutlined />} danger onClick={() => remove(name)} />
                      </Tooltip>
                    </Col>
                  </Row>
                </>
              ))}
              <Form.Item wrapperCol={{ span: 0 }}>
                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                  Add topic
                </Button>
              </Form.Item>
              <Form.ErrorList errors={errors} />
            </>
          )}
        </Form.List>
      </Form>
    </Modal>
  );
}

export default TemplateModal;
