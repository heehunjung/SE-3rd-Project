import React, { useEffect, useState } from 'react';
import { Nav, Container, Form, Button, Row, Col, Table, InputGroup } from 'react-bootstrap';
import Navbar from 'react-bootstrap/Navbar';
import { useParams } from 'react-router-dom';
import '../App.css';
import Card from "react-bootstrap/Card";

const Admin = () => {
    const { id } = useParams();
    const [users, setUsers] = useState([]);
    const [posts, setPosts] = useState([]);
    const [stocks, setStocks] = useState([]);
    const [newStock, setNewStock] = useState({ stockName: '', currentPrice: '' });
    const [amount, setAmount] = useState(0);

    useEffect(() => {
        fetch(`http://localhost:8080/members`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => setUsers(data))
            .catch(error => console.error('Error fetching members data:', error));

        fetch(`http://localhost:8080/title/username`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => setPosts(data))
            .catch(error => console.error('Error fetching posts data:', error));

        fetch(`http://localhost:8080/stockData`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => setStocks(data))
            .catch(error => console.error('Error fetching stocks data:', error));
    }, []);

    const handleStockChange = (e) => {
        const { name, value } = e.target;
        setNewStock({ ...newStock, [name]: value });
    };

    const handleAddStock = () => {
        fetch('http://localhost:8080/stocks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(newStock),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                setStocks([...stocks, data]);
                setNewStock({ stockName: '', currentPrice: '' });
            })
            .catch(error => console.error('Error adding stock:', error));
    };

    const handleDeletePost = (postId) => {
        fetch(`http://localhost:8080/delete/${postId}`, {
            method: 'DELETE',
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                alert(data);
                fetch(`http://localhost:8080/title/username`)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => setPosts(data))
                    .catch(error => console.error('Error fetching posts data:', error));
            })
            .catch(error => console.error('Error deleting post:', error));
    };

    const handleAmountChange = (e) => {
        setAmount(parseInt(e.target.value, 10));
    };

    const handleUpdateBalance = (userId) => {
        fetch(`http://localhost:8080/updateBalance/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(amount),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                setUsers(users.map(user => (user.id === userId ? data : user)));
            })
            .catch(error => console.error('Error updating balance:', error));
    };

    const handleDeleteUser = (userId) => {
        fetch(`http://localhost:8080/member/${userId}`, {
            method: 'DELETE',
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                alert(data);
                setUsers(users.filter(user => user.id !== userId));
            })
            .catch(error => console.error('Error deleting user:', error));
    };

    return (
        <>
            <Navbar bg="dark" variant="dark">
                <Container>
                    <Navbar.Brand href={`/Home/${id}`}>KW 거래소📉 관리자 모드</Navbar.Brand>
                    <Nav className="ml-auto">
                        <Nav.Link href={`/Home/${id}`}>홈 화면</Nav.Link>
                        <Nav.Link href={`/Trading/${id}`}>주식 구매</Nav.Link>
                        <Nav.Link href={`/Board/${id}`}>커뮤니티</Nav.Link>
                        <Nav.Link href={`/Post/${id}`}>게시글 작성</Nav.Link>
                        <Nav.Link href={`/Admin/${id}`}>관리자 모드</Nav.Link>
                        <Nav.Link href={'/Login'}>로그아웃</Nav.Link>
                    </Nav>
                </Container>
            </Navbar>
            <Container>
                <Row>
                    <Col>
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Header><strong>게시글 관리</strong></Card.Header>
                            <Card.Body>
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    fontWeight: 'bold',
                                    marginBottom: '10px'
                                }}>
                                    <div style={{flex: 1, textAlign: 'left'}}>제목</div>
                                    <div style={{flex: 1, textAlign: 'left'}}>ID</div>
                                    <div style={{flex: 1, textAlign: 'left'}}>작성자</div>
                                    <div style={{flex: 1, textAlign: 'left'}}>관리</div>
                                </div>
                                <ul className="scrollable-card5" style={{listStyleType: 'none', padding: 0}}>
                                    {posts.map(post => (
                                        <li key={post.id} style={{
                                            display: 'flex',
                                            justifyContent: 'space-between',
                                            alignItems: 'center',  // 요소들을 수직 중앙 정렬
                                            marginBottom: '5px',
                                            padding: '5px'
                                        }}>
                                            <div style={{flex: 1, textAlign: 'left'}}>{post.title}</div>
                                            <div style={{flex: 1, textAlign: 'left'}}>{post.username}</div>
                                            <div style={{flex: 1, textAlign: 'left'}}>{post.nickname}</div>
                                            <div style={{flex: 1, textAlign: 'left'}}>
                                                <Button className="btn-icon" style={{marginLeft: 'auto'}}
                                                        onClick={() => handleDeletePost(post.postId)}>
                                                    ❌
                                                </Button>
                                            </div>
                                        </li>
                                    ))}
                                </ul>

                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <h2>유저 관리</h2>
                        <InputGroup className="mb-3">
                            <Form.Control
                                placeholder="금액 입력"
                                aria-label="금액 입력"
                                aria-describedby="basic-addon2"
                                type="number"
                                onChange={handleAmountChange}
                            />
                        </InputGroup>
                        <Table striped bordered hover>
                            <thead>
                            <tr>
                                <th>닉네임</th>
                                <th>잔액</th>
                                <th>관리</th>
                            </tr>
                            </thead>
                            <tbody>
                            {users.map(user => (
                                <tr key={user.id}>
                                    <td>{user.nickname}</td>
                                    <td>{user.balance}</td>
                                    <td>
                                            <Button variant="primary" onClick={() => handleUpdateBalance(user.id)}>금액 추가/삭제</Button>
                                            <Button variant="danger" onClick={() => handleDeleteUser(user.id)}>삭제</Button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </Col>
                </Row>
                <Row>
                    <Col>
                        <h2>주식 관리</h2>
                        <Form>
                            <Form.Group>
                                <Form.Label>주식 이름</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="주식 이름 입력"
                                    name="stockName"
                                    value={newStock.stockName}
                                    onChange={handleStockChange}
                                />
                            </Form.Group>
                            <Form.Group>
                                <Form.Label>주식 가격</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="주식 가격 입력"
                                    name="currentPrice"
                                    value={newStock.currentPrice}
                                    onChange={handleStockChange}
                                />
                            </Form.Group>
                            <Button variant="primary" onClick={handleAddStock}>추가</Button>
                        </Form>
                        <div style={{ maxHeight: '300px', overflowY: 'scroll' }}>
                            <Table striped bordered hover>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>이름</th>
                                        <th>가격</th>
                                        <th>관리</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {stocks.map(stock => (
                                        <tr key={stock.id}>
                                            <td>{stock.id}</td>
                                            <td>{stock.stockName}</td>
                                            <td>{stock.currentPrice}</td>
                                            <td>
                                                <Button variant="danger">삭제</Button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </div>
                    </Col>
                </Row>
            </Container>
        </>
    );
};

export default Admin;
