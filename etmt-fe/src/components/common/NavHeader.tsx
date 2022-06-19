import { Menu, notification } from "antd";
import { Header } from "antd/lib/layout/layout";
import { FunctionComponent } from "react";
import { Link } from "react-router-dom";
import { SUCCESS_MSG } from "../../common/Common";
import { useAuth } from "../auth/Auth";

interface Props {
  onSignOut: () => void;
}

export const AuthNav: FunctionComponent<Props> = (props: Props) => {
  const auth = useAuth();

  const handleSignOut = () => {
    auth.signOut()
      .then(() => {
        notification.success({
          message: SUCCESS_MSG,
          description: 'You have successfully signed out!'
        });
      }).catch(error => {
        console.log(error);
      })
    props.onSignOut();
  }

  return (
    <Menu data-testid="auth-nav-menu" theme="dark" mode="horizontal" defaultSelectedKeys={['1']}>
      <Menu.Item key="1">
        <Link to="/">
          App
        </Link>
      </Menu.Item>
      <Menu.Item data-testid="auth-nav-menu-login" key="2" disabled={auth.isSignedIn()}>
        <Link to="/login">
          Sign in
        </Link>
      </Menu.Item>
      <Menu.Item key="3" disabled={auth.isSignedIn()}>
        <Link to="/signup">
          Sign up
        </Link>
      </Menu.Item>
      <Menu.Item data-testid="auth-nav-menu-logout" key="4" onClick={handleSignOut} disabled={!auth.isSignedIn()}>
        Sign out ({auth.user?.username})
      </Menu.Item>
    </Menu>
  );
}

const NavHeader: FunctionComponent<Props> = (props: Props) => {
  return (
    <Header>
      <b className="logo">ETMT</b>
      <AuthNav onSignOut={props.onSignOut} />
    </Header>
  );
}

export default NavHeader;
