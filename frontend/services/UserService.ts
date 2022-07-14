import { apiUrl } from "./ProductService";

export interface User {
  username: string,
  role: string,
  jwtToken: string
}

export async function login(username: string, password: string): Promise<void> {
  return fetch(`${apiUrl}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })
    .then(res => {
      if (!res.ok) {
        throw new Error('Invalid username/password');
      }
      res.json().then(u => localStorage.setItem('user', JSON.stringify(u)));
    });
}

export async function register(username: string, password: string): Promise<void> {
  console.log(username, password)
  return fetch(`${apiUrl}/users`, {
    method: 'POST',
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  })
    .then(res => {
      if (!res.ok) {
        throw new Error('Invalid username/password');
      }
    })
}

export function logout() {
  localStorage.removeItem('user');
}

export function getUser() {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
}