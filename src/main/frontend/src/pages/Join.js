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

        // 닉네임 중복 체크 먼저 수행
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
                    return response.text(); // 사용 가능한 닉네임에 대한 메시지 반환
                }
            })
            .then((message) => {
                console.log(message);

                // 닉네임 사용 가능 시 가입 요청
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
                    return res.json();
                } else {
                    return res.text().then((text) => Promise.reject(text));
                }
            })
            .then((data) => {
                console.log(data);
                navigate('/');
            })
            .catch((error) => {
                console.error('Error:', error);
                alert(error); // 오류 메시지 팝업 표시
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
