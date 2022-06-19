import { DeleteOutlined, InfoCircleOutlined, PlusOutlined } from "@ant-design/icons";
import { Button, Checkbox, Col, Divider, Form, FormInstance, Input, InputNumber, Modal, notification, Radio, Row, Select, Tooltip, Typography } from "antd";
import TextArea from "antd/lib/input/TextArea";
import React, { FunctionComponent, useEffect, useState } from "react";
import { apiPostItem } from "../../../common/APIUtils";
import { Answer, ERR_MSG, ItemType, Question, QuestionType, SUCCESS_MSG } from "../../../common/Common";
import { useAuth } from "../../auth/Auth";
import { ItemModalProps } from "../items/ItemInfo";

const { Option } = Select;
const { Text } = Typography;

const QuestionModal: FunctionComponent<ItemModalProps> = (props: ItemModalProps) => {
  const formRef = React.createRef<FormInstance<Question>>();

  const auth = useAuth();
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [initialValues, setInitialValues] = useState<any>({ questionType: QuestionType.ShortAnswer } as Question);

  const [questionType, setQuestionType] = useState<QuestionType>(QuestionType.ShortAnswer);

  useEffect(() => {
    setInitialValues(props.modal.modalState.isEdit ? props.selectedItemData : { questionType: QuestionType.ShortAnswer });
    setQuestionType(props.modal.modalState.isEdit ? props.selectedItemData.questionType : QuestionType.ShortAnswer)

    console.log("item data before: ", props.selectedItemData?.answers);

    if (props.selectedItemData?.answers) {
      let answers = props.selectedItemData.answers.map((answer: Answer) => {
        return {
          text: answer.text,
          isDefault: answer.isCorrect
        }
      })

      let data = Object.assign({}, props.selectedItemData);
      data.answers = answers;
      console.log("transformed item data: ", data.answers);
      setInitialValues(props.modal.modalState.isEdit ? data : { questionType: QuestionType.ShortAnswer });
    }
  }, [props.modal.modalState.isVisible])

  useEffect(() => {
    console.log("updated initial values: ", initialValues);
  }, [initialValues])

  const handleOk = (formValues: any) => {
    var item: Question = formValues;
    item.type = ItemType.question;

    // transform asnwers
    item.answers = item.answers.map((answer: any) => {
      return {
        text: answer.text,
        isCorrect: questionType === QuestionType.ShortAnswer ? true : answer.isDefault
      }
    })

    if (props.modal.modalState.isEdit) {
      item.id = initialValues.id;
      item.parentId = initialValues.parentId;
    } else {
      // if in add mode, parent category is selected
      item.parentId = props.selectedItemData?.id;
    }

    // console.log(`post question form values`, formValues);
    console.log(`post question (${props.modal.modalState.isEdit ? "edit" : "add"}): `, item);

    setConfirmLoading(true);

    apiPostItem(item, auth.user?.token)
      .then(() => {
        notification.success({
          message: SUCCESS_MSG,
          description: props.modal.modalState.isEdit ? 'Question edited!' : 'Question added!'
        });
        props.modal.setModalState({ isVisible: false, isEdit: false });
        props.onModalClose();
      }).catch(error => {
        console.error(error);
        notification.error({
          message: ERR_MSG,
          description: 'There was an error while posting a question... Please try again.'
        })
      }).finally(() => setConfirmLoading(false));
  };

  const handleCancel = () => {
    props.modal.setModalState({ isVisible: false, isEdit: false });
  };

  const onSelectValuesChange = (type: QuestionType) => {
    setQuestionType(type);
    console.log("select value changed to: ", questionType)

    // disable all radios except the first one when switching to single choice
    if (type === QuestionType.SingleChoice) {
      let radios = formRef.current?.getFieldValue("answers");
      if (radios) {
        for (let i = 0; i < radios.length; i++) {
          if (i === 0) {
            radios[i].isDefault = true;
          } else {
            radios[i].isDefault = false;
          }
        }
        formRef.current?.setFieldsValue(radios);
      }
    }
  };

  const handleSubmitFailed = (errorInfo: any) => {
    console.log("submit failed: ", errorInfo);
  }

  const getCheckboxFormItemForType = (type: QuestionType, field: any) => {
    switch (type) {
      case QuestionType.ShortAnswer:
        return <></>
      case QuestionType.SingleChoice:
        return <Form.Item
          {...field.restField}
          name={[field.name, 'isDefault']}
          fieldKey={[field.key, "isDefault"]}
          valuePropName="checked"
        >
          <Radio onChange={
            () => {
              let formCheckboxes = formRef.current?.getFieldValue("answers")
              formCheckboxes = formCheckboxes.map((answer: any, answerKey: number) => {
                if (field.key === answerKey) {
                  answer.isDefault = true;
                } else {
                  answer.isDefault = false;
                }
                return answer;
              })
              formRef.current?.setFieldsValue({ answers: formCheckboxes })
            }
          }>Is correct</Radio>
        </Form.Item>
      case QuestionType.MultipleChoice:
        return <Form.Item
          {...field.restField}
          name={[field.name, 'isDefault']}
          valuePropName="checked"
        >
          <Checkbox>Is correct</Checkbox>
        </Form.Item>
    }
  }

  const getTooltipForQuestionType = (type: QuestionType) => {
    var text = "";
    switch (type) {
      case QuestionType.ShortAnswer:
        text = "Short answer questions can have multiple answers. It is assumed that all defined answers are correct"
        break;
      case QuestionType.SingleChoice:
        text = "Single choice questions can only have a single correct answer"
        break;
      case QuestionType.MultipleChoice:
        text = "Multiple choice questions can have multiple correct answers. The weight of each answer is calculated automatically before export into XML for Moodle"
        break;
    }
    return <p className="infoMsg"><InfoCircleOutlined /> {text}</p>
  }

  return (
    <Modal
      title={
        props.modal.modalState.isEdit ?
          "Edit question: " + props.selectedItemData?.title :
          "Add question under category: " + props.selectedItemData?.title
      }
      visible={props.modal.modalState.isVisible}
      confirmLoading={confirmLoading}
      onCancel={handleCancel}
      destroyOnClose={true}
      footer={[
        <Button type="primary" form="questionForm" key="submit" htmlType="submit" loading={confirmLoading}>
          Submit
        </Button>
      ]}
      width={"800px"}
    >

      <Form
        name="questionForm"
        labelCol={{ span: 4 }}
        layout="horizontal"
        initialValues={initialValues}
        onFinish={handleOk}
        onFinishFailed={handleSubmitFailed}
        ref={formRef}
      >

        <Form.Item label="Question Type" name="questionType">
          <Select defaultValue={QuestionType.ShortAnswer} style={{ width: 200 }} onChange={onSelectValuesChange}>
            <Option value={QuestionType.ShortAnswer}>Short Answer</Option>
            <Option value={QuestionType.SingleChoice}>Single Choice</Option>
            <Option value={QuestionType.MultipleChoice}>Multiple Choice</Option>
          </Select>
        </Form.Item>

        <Text>{getTooltipForQuestionType(questionType)}</Text>
        <Divider />

        <Form.Item
          label="Title"
          name="title"
          rules={[{ required: true, message: 'Question title cannot be empty!' }]}
        >
          <Input autoFocus/>
        </Form.Item>

        <Form.Item
          label="Text"
          name="text"
        >
          <TextArea style={{ minHeight: "200px" }} />
        </Form.Item>

        <Form.Item
          label="Points"
          name="points"
          rules={[{ required: true, message: "Question needs to award at least 1 point!" }]}
        >
          <InputNumber min={1} />
        </Form.Item>

        <Form.Item
          label="Penalty"
          name="penalty"
        >
          <InputNumber min={0} />
        </Form.Item>

        <Form.List
          name="answers"
          rules={[
            {
              validator: async (_, answers) => {
                if (answers == null || answers.length < 1) {
                  return Promise.reject(new Error('At least 1 answer is required!'));
                }
              }
            }
          ]}
        >
          {(fields, { add, remove }, { errors }) => (
            <>
              {fields.map(({ key, name, ...restField }) => (
                <>
                  <Divider orientation="left" orientationMargin="25px">
                    Answer #{key + 1}
                  </Divider>
                  <Row>
                    <Col flex="auto">
                      <Form.Item
                        label="Answer text"
                        labelCol={{ span: 5 }}
                        {...restField}
                        name={[name, 'text']}
                        rules={[
                          {
                            validator: async (_, value) => {
                              if (!value || value.length < 1) {
                                return Promise.reject(new Error('Answer text cannot be empty!'));
                              }
                            },
                            required: true,
                            message: 'Missing answer text'
                          }
                        ]}
                      >
                        <TextArea style={{ minHeight: "50px" }} />
                      </Form.Item>
                    </Col>
                    <Col flex="20px">
                    </Col>
                    <Col flex="100px">
                      {getCheckboxFormItemForType(questionType, { key, name, ...restField })}
                    </Col>
                    <Col flex="30px">
                      <Tooltip title="Remove">
                        <Button type="primary" shape="circle" icon={<DeleteOutlined />} danger onClick={() => remove(name)} />
                      </Tooltip>
                    </Col>
                  </Row>
                </>
              ))}

              <Form.Item>
                <Button type="dashed" onClick={() => {
                  add()
                }} block icon={<PlusOutlined />}>
                  Add answer
                </Button>
              </Form.Item>
              
              <Form.ErrorList errors={errors} />
            </>
          )}
        </Form.List>

      </Form>
    </Modal >
  );
}



export default QuestionModal;
