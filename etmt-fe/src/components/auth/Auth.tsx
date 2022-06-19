import { createContext, ReactNode, useContext, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { apiSignIn } from "../../common/APIUtils";
import { Nullable, UserCredentialsPayload } from "../../common/Common";

export interface UserAuthInfo {
  username: string,
  token: Nullable<string>
}

export const USER_ANONYMOUS: UserAuthInfo = {
  username: "Anonymous",
  token: null
};

export interface AuthContextType {
  user: UserAuthInfo,
  signIn: (requestData: UserCredentialsPayload) => Promise<void>,
  signOut: () => Promise<void>,
  isSignedIn: () => boolean
}

const authContext = createContext<AuthContextType>(null!);

export const useAuth = () => {
  return useContext(authContext);
}

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<UserAuthInfo>(USER_ANONYMOUS);

  const signIn = (userCredentials: UserCredentialsPayload) => {
    return apiSignIn(userCredentials)
      .then(response => {
        setUser({
          username: userCredentials.username,
          token: response.accessToken
        });
      }).catch(error => {
        return Promise.reject(error);
      })
  }

  const signOut = () => {
    return new Promise<void>(() => {
      setUser(USER_ANONYMOUS);
    });
  }

  const isSignedIn = () => {
    return user.token != null;
  }

  return <authContext.Provider value={{ user, signIn, signOut, isSignedIn: isSignedIn }}>{children}</authContext.Provider>
}

export const RequireAuth = ({ children }: { children: JSX.Element }) => {
  const auth = useAuth();
  const location = useLocation();

  if (!auth.isSignedIn()) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return children;
}
