import { Button, Form, Input, notification, Row } from "antd";
import Title from "antd/lib/typography/Title";
import { FunctionComponent } from "react";
import { apiCheckUsernameAvailability, apiSignUp } from "../../common/APIUtils";
import { ERR_MSG, SUCCESS_MSG, UserCredentialsPayload } from "../../common/Common";

interface SignupProps {
  onSignUp: () => void;
}

const Signup: FunctionComponent<SignupProps> = (props: SignupProps) => {
  const onSignup = (userCredentials: UserCredentialsPayload) => {
    console.log('signup: ', userCredentials);

    apiSignUp(userCredentials)
      .then(response => {
        console.log(response);

        notification.success({
          message: SUCCESS_MSG,
          description: 'You have successfully signed up!'
        });

        props.onSignUp();
      }).catch(errReponse => {
        console.log(errReponse);

        let description = 'Sorry! Something went wrong. Please try again!';
        if (errReponse.status === 400) {
          description = 'Username already exists, please try another one.';
        }

        notification.error({
          message: ERR_MSG,
          description: description
        });
      });
  }

  const onSignupFailed = (errorInfo: any) => {
    console.log('signup failed: ', errorInfo);
  }

  const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
  };

  return (
    <Row justify="center" align="middle" style={{ minHeight: "90vh", background: "white" }}>
      <Form
        name="signup"
        {...layout}
        onFinish={onSignup}
        onFinishFailed={onSignupFailed}
        autoComplete="off"
        style={{ width: 300}}
      >
        <Title level={3} style={{ padding: "5px" }}>Sign up</Title>
        <Form.Item
          label="Username"
          name="username"
          rules={[
            () => ({
              validator(_, value) {
                if (!value) {
                  return Promise.reject('Please enter your username!');
                }

                // TODO: this is currently happening every key press, which is a bit too many requests ..
                return apiCheckUsernameAvailability(value)
                  .then((response) => {
                    if (response.true) {
                      console.log(`username ${value} is available`);
                      return Promise.resolve();
                    }
                    return Promise.reject('This username is not available');
                  });
              }
            })
          ]}
        >
          <Input />
        </Form.Item>

        <Form.Item
          label="Password"
          name="password"
          rules={[{ required: true, message: 'Please enter a password with length from 3 to 30 characters!', min: 3, max: 30 }]}
        >
          <Input.Password />
        </Form.Item>

        <Form.Item {...layout}>
          <Button type="primary" htmlType="submit">
            Sign up
          </Button>
        </Form.Item>
      </Form>
    </Row>

  );
}

export default Signup;
