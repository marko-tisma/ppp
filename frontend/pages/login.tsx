import { useRouter } from "next/router";
import { MouseEvent, useCallback, useState } from "react";
import { login, register } from "../services/UserService";
import style from "../styles/Login.module.css";

const Login = () => {

  const router = useRouter();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loginError, setLoginError] = useState<Error | null>(null);
  const [registerError, setRegisterError] = useState<Error | null>(null);
  const [registered, setRegistered] = useState(false);

  const onLogin = useCallback((e: MouseEvent) => {
    e.preventDefault();
    setLoginError(null);
    setRegisterError(null);
    setRegistered(false);

    login(username, password)
      .then(() => {
        const returnUrl = '/';
        router.push(returnUrl);
      })
      .catch(error => {
        setLoginError(error);
      })
  }, [username, password]);

  const onRegister = useCallback((e: MouseEvent) => {
    e.preventDefault();
    setLoginError(null);
    setRegisterError(null);
    setRegistered(false);

    register(username, password)
      .then(() => {
        setRegistered(true);
      })
      .catch(error => {
        setRegisterError(error);
      })
  }, [username, password]);

  const onChange = useCallback((setFunction: Function, value: string) => {
    setFunction(value);
    setLoginError(null);
    setRegisterError(null);
    setRegistered(false);
  }, []);

  return (
    <div className={style.container}>
      <div className={style.home} onClick={() => router.push('/')}>🏠</div>
      <form className={style.form}>
        <label>
          <p className={style.label}>Username</p>
          <input type="text" className={`${style.input} ${loginError || registerError ? style.inputError : ''}`} onChange={e => onChange(setUsername, e.target.value)} />
        </label>
        <label>
          <p className={style.label}>Password</p>
          <input type="password" className={`${style.input} ${loginError || registerError ? style.inputError : ''}`} onChange={e => onChange(setPassword, e.target.value)} />
        </label>
        {loginError &&
          <div className={style.errorMessage}>{loginError?.message}</div>
        }
        {registerError &&
          <div className={style.errorMessage}>{registerError?.message}</div>
        }
        {registered &&
          <div className={style.registeredMessage}>You can now login</div>
        }
        <button onClick={onLogin} className={style.loginBtn}>Login</button>
        <button onClick={onRegister} className={style.registerBtn}>Register</button>
      </form>
    </div>
  )
}

export default Login;