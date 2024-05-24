import React from 'react';
import {Link} from "react-router-dom";
import { Button, Form } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useState } from 'react';
import {useNavigate} from 'react-router-dom';

function Login() {
    const navigate = useNavigate();

    const [loginData, setLoginData] = useState({
        username: '',
        password: ''
    }); // 객체 생성

    const handleChange =(e)=> {
        setLoginData({
            ...loginData,
            [e.target.name]: e.target.value
        });
    } // 사용자의 입력에 따라 객체에 값을 넣는다.

    const handleSubmit =(e)=> {
        e.preventDefault(); // 폼이 제출될 때 자동적으로 새로 고침 되는 것을 방지
        fetch('http://localhost:8080/login',{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=utf-8',
            },
            body: JSON.stringify(loginData), // 실제 데이터를 포함 loginData를 JSON 문자열로 변환하여 서버에 보냄
        })
            .then(response=>response.json())
            .then((data)=>{
                if(data.id) {
                    navigate(`/home/${data.id}`);
                } else {
                    alert("로그인에 실패하였습니다.");
                }
            })
            .catch((error)=>{
                console.error('Error',error);
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
