import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, Form } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

function Login() {
    const navigate = useNavigate();

    const [loginData, setLoginData] = useState({
        username: '',
        password: ''
    });

    const handleChange = (e) => {
        setLoginData({
            ...loginData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=utf-8',
            },
            body: JSON.stringify(loginData),
        })
            .then(response => {
                console.log("test")
                console.log(response.headers);  // 응답 헤더를 로그로 출력

                if (!response.ok) {
                    throw new Error('로그인에 실패하였습니다.');
                }
                // 헤더에서 토큰을 가져옵니다.
                const accessToken = response.headers.get('Authorization');
                const refreshToken = response.headers.get('Authorization-Refresh');

                if (!accessToken || !refreshToken) {
                    throw new Error('토큰을 가져오지 못했습니다.');
                }

                // 토큰을 세션 또는 로컬 스토리지에 저장
                sessionStorage.setItem('accessToken', accessToken);
                sessionStorage.setItem('refreshToken', refreshToken);

                // 홈 페이지로 이동
                navigate('/home');
            })
            .catch(error => {
                console.error('Error:', error);
                alert('로그인 중 오류가 발생했습니다.');
            });
    };

    return (
        <div className="d-flex justify-content-center align-items-center vh-100">
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formBasicid">
                    <Form.Label>아이디</Form.Label>
                    <Form.Control
                        type="text"
                        name="username"
                        placeholder="id"
                        value={loginData.username}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label>비밀번호</Form.Label>
                    <Form.Control
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={loginData.password}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Text className="text-muted">
                        아이디가 없을 경우 회원 가입해주세요.
                    </Form.Text>
                </Form.Group>
                <Button variant="primary" type="submit">
                    로그인
                </Button>
                {'   '}
                <Link to="/Join">
                    <Button variant="primary">
                        회원 가입
                    </Button>
                </Link>
            </Form>
        </div>
    );
}

export default Login;
