import React from 'react';
import { useState } from 'react';
import {useNavigate} from 'react-router-dom';
import { Form, Button } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

const Join = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        number: '',
        username: '',
        password: '',
        nickname: ''
    });

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        fetch(`http://localhost:8080/nickname/${formData.nickname}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json; charset=utf-8',
            }
        })
            .then((response) => {
                if (response.ok) {
                    return response.text().then((text) => Promise.reject("중복된 닉네임입니다."));
                } else {
                    return response.text();
                }
            })
            .then((message) => {
                console.log("닉네임 검증 통과:", message);

                return fetch('http://localhost:8080/join', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json; charset=utf-8',
                    },
                    body: JSON.stringify(formData),
                });
            })
            .then((res) => {
                if (res.status === 201) {
                    return res.text();
                } else {
                    return res.text().then((text) => Promise.reject(text));
                }
            })
            .then((data) => {
                console.log("가입 성공:", data);
                alert(data);
                navigate('/login');
            })
            .catch((error) => {
                console.error('오류 발생:', error);
                alert(error);
            });
    };


    return (
        <div className="d-flex justify-content-center align-items-center vh-100">
            <Form onSubmit={handleSubmit} className="signup-container">
                <div>회원가입</div>
                <Form.Group className="form-field mb-3">
                    <Form.Control
                        type="text"
                        name="name"
                        placeholder="이름"
                        required
                        autoFocus
                        value={formData.name}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group className="form-field mb-3">
                    <Form.Control
                        type="tel"
                        name="number"
                        placeholder="전화번호"
                        required
                        value={formData.tel}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group className="form-field mb-3">
                    <Form.Control
                        type="text"
                        name="username"
                        placeholder="아이디"
                        required
                        value={formData.username}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group className="form-field mb-3">
                    <Form.Control
                        type="password"
                        name="password"
                        placeholder="비밀번호"
                        required
                        value={formData.password}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group className="form-field mb-3">
                    <Form.Control
                        type="text"
                        name="nickname"
                        placeholder="닉네임"
                        required
                        autoFocus
                        value={formData.nickname}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Button variant="primary" type="submit">
                    가입하기
                </Button>{' '}
                <Button variant="secondary" onClick={() => navigate('/')}>
                    로그인 페이지로
                </Button>
            </Form>
        </div>
    );
};

export default Join;
