import { FunctionComponent } from 'react';

import { Button, Form, Input, notification, Row, Typography } from 'antd';
import { ERR_MSG, SUCCESS_MSG, UserCredentialsPayload } from '../../common/Common';
import { useAuth } from './Auth';

const { Title } = Typography;

interface LoginProps {
  onSignIn: () => void
}

const Login: FunctionComponent<LoginProps> = (props: LoginProps) => {
  return (
    <LoginForm onSignIn={props.onSignIn} />
  );
}

interface LoginFormProps {
  onSignIn: () => void;
}

const LoginForm: FunctionComponent<LoginFormProps> = (props: LoginFormProps) => {
  const auth = useAuth();

  const handleSignIn = (userCredentials: UserCredentialsPayload) => {
    console.log('login: ', userCredentials);

    auth.signIn(userCredentials)
      .then(() => {
        notification.success({
          message: SUCCESS_MSG,
          description: 'You have signed in!'
        });
        props.onSignIn();
      }).catch(error => {
        console.error(error);

        let description = 'Sorry! Something went wrong. Please try again!';
        if (error.status === 401) {
          description = 'Your Username or Password is incorrect. Please try again!';
        }

        notification.error({
          message: ERR_MSG,
          description: description
        });
      });
  }

  const handleSignInFailed = (errorInfo: any) => {
    console.log('login failed: ', errorInfo);
  }

  const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 },
  };

  return (
    <Row justify="center" align="middle" style={{ minHeight: "90vh", background: "white" }}>
      <Form
        name="login"
        {...layout}
        onFinish={handleSignIn}
        onFinishFailed={handleSignInFailed}
        autoComplete="off"
        style={{ width: 300}}
      >
        <Title level={3} style={{ padding: "5px" }}>Login</Title>
        <Form.Item
          label="Username"
          name="username"
          rules={[{ required: true, message: 'Please input your username!' }]}
        >
          <Input />
        </Form.Item>

        <Form.Item
          label="Password"
          name="password"
          rules={[{ required: true, message: 'Please input your password!' }]}
        >
          <Input.Password />
        </Form.Item>

        <Form.Item {...layout}>
          <Button type="primary" htmlType="submit">
            Log in
          </Button>
        </Form.Item>
      </Form>
    </Row>
  );
}

export default Login;
