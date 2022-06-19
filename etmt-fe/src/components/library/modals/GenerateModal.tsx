import { ExclamationCircleOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { Button, Modal, notification } from "antd";
import { FunctionComponent, useEffect, useState } from "react";
import { apiCheckQuestionCount, apiGenerateVariants } from "../../../common/APIUtils";
import { ERR_MSG, GenerationStrategy, SUCCESS_MSG, Template } from "../../../common/Common";
import { useAuth } from "../../auth/Auth";
import { ItemModalProps } from "../items/ItemInfo";

const GenerateModal: FunctionComponent<ItemModalProps> = (props: ItemModalProps) => {
  const auth = useAuth();
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [checkLoading, setCheckLoading] = useState(false);
  const [errors, setErrors] = useState(new Array<string>());

  useEffect(() => {
    checkQuestionAvailability();
  }, [props.modal.modalState.isVisible])

  useEffect(() => {
    console.log("updated errors state: ", errors)
  }, [errors])

  const onGenerate = (strategy: GenerationStrategy) => {
    console.log("generate tests for template: ", props.selectedItemData);

    setConfirmLoading(true);

    apiGenerateVariants(props.selectedItemData?.id, strategy, auth.user?.token)
      .then(() => {
        notification.success({
          message: SUCCESS_MSG,
          description: `Variants generated using ${strategy} strategy!`
        });
        props.modal.setModalState({ isVisible: false, isEdit: false });
        props.onModalClose();
      }).catch(error => {
        console.error(error);
        notification.error({
          message: ERR_MSG,
          description: 'There was an error while generating test variants... Please try again.'
        })
      }).finally(() => setConfirmLoading(false))
  }

  const onCancel = () => {
    props.modal.setModalState({ isVisible: false, isEdit: false });
  }

  const checkQuestionAvailability = () => {
    setCheckLoading(true);
    setErrors([])
    if (props.selectedItemData) {
      var data = props.selectedItemData as Template;
      var errs = new Array<string>();
      Promise.all(data.topics.map(topic => {
        let points = topic.points ? topic.points : -1;
        let categoryIds = topic.categories ? [...topic.categories] : [];

        // if no category ids are specified, take the parent category
        if (categoryIds.length < 1) {
          categoryIds.push([{ title: "Root", id: data.parentId }]);
        }

        return Promise.all(categoryIds.map(ids => {
          return apiCheckQuestionCount(ids[ids.length - 1].id, points, topic.questionCount, auth.user?.token)
            .then((response) => {
              console.log("variants can be generated for category " + response.categoryTitle + ": ", response.sufficient);
              if (!response.sufficient) {
                if (points != -1) {
                  errs.push("Insufficient number of " + points + " point questions under category " + response.categoryTitle)
                } else {
                  errs.push("Insufficient number of questions under category " + response.categoryTitle)
                }
              }
            }).catch(error => {
              console.error(error);
            })
        }))
      })).finally(() => {
        setCheckLoading(false)
        setErrors(errs)
      })
    }
  }

  return (
    <Modal
      title={"Generate test variants from: " + props.selectedItemData?.title}
      visible={props.modal.modalState.isVisible}
      confirmLoading={confirmLoading}
      onCancel={onCancel}
      footer={[
        <Button type="default" loading={checkLoading} onClick={checkQuestionAvailability}>
          Re-check
        </Button>,
        <Button type="primary" loading={confirmLoading} onClick={() => onGenerate(GenerationStrategy.unique)} disabled={errors.length > 0}>
          Generate Unique
        </Button>,
        <Button type="primary" loading={confirmLoading} onClick={() => onGenerate(GenerationStrategy.compromise)} disabled={errors.length > 0}>
          Generate Compromise
        </Button>
      ]}
    >
      <p className="infoMsg"><InfoCircleOutlined /> Test variants will be generated using the selected template. All variants created will be placed under a new category named <i>'Test variants - {props.selectedItemData?.title}'</i>.</p>
      {errors.map(error => <p className="errorMsg"><ExclamationCircleOutlined /> {error}</p>)}
    </Modal>
  );
}

export default GenerateModal;
