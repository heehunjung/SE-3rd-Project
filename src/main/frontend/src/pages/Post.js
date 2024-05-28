import React from 'react';
import Navbar from "react-bootstrap/Navbar";
import {Container, Form, Nav} from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Card from "react-bootstrap/Card";

const Post = () => {
    return (
        <>
            <Navbar bg="dark" data-bs-theme="dark">
                <Container>
                    <Navbar.Brand href="/Home">KW 거래소</Navbar.Brand>
                    <Nav className="ml-auto">
                        <Nav.Link href="/Home">홈 화면</Nav.Link>
                        <Nav.Link href="/Trading">주식 구매</Nav.Link>
                        <Nav.Link href="/Board">커뮤니티</Nav.Link>
                        <Nav.Link href="/MyInfo">내 정보</Nav.Link>
                    </Nav>
                </Container>
            </Navbar>
            <Form>
                <Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
                    <Form.Label>title</Form.Label>
                    <Form.Control type="title" placeholder="제목을 입력해주세요" />
                </Form.Group>
                <Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
                    <Form.Label>content</Form.Label>
                    <Form.Control as="textarea" rows={5} />
                </Form.Group>
            </Form>
        </>
    );}

export default Post;