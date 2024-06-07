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

    const handleDeleteStock = (stockId) => {
        fetch(`http://localhost:8080/stocks/${stockId}`, {
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
                setStocks(stocks.filter(stock => stock.id !== stockId));
            })
            .catch(error => console.error('Error deleting stock:', error));
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
                                                        onClick={() => handleDeletePost(post.id)}>
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
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Header><strong>유저 관리</strong></Card.Header>
                            <Card.Body>
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    fontWeight: 'bold',
                                    marginBottom: '10px'
                                }}>
                                    <div style={{flex: 1, textAlign: 'left'}}>닉네임</div>
                                    <div style={{flex: 1, textAlign: 'left'}}>잔액</div>
                                    <div style={{flex: 1, textAlign: 'left'}}>관리</div>
                                </div>
                                <ul className="scrollable-card5" style={{listStyleType: 'none', padding: 0}}>
                                    {users.map(user => (
                                        <li key={user.id} style={{
                                            display: 'flex',
                                            justifyContent: 'space-between',
                                            alignItems: 'center',  // 요소들을 수직 중앙 정렬
                                            marginBottom: '5px',
                                            padding: '5px'
                                        }}>
                                            <div style={{flex: 1, textAlign: 'left'}}>{user.nickname}</div>
                                            <div style={{flex: 1, textAlign: 'left'}}>{user.balance}</div>
                                            <div style={{flex: 1, textAlign: 'left'}}>
                                                <Button className="btn-icon" style={{marginLeft: 'auto'}} onClick={() => handleDeleteUser(user.id)}>❌</Button>
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
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Header><strong>주식 관리</strong></Card.Header>
                            <Card.Body>
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    fontWeight: 'bold',
                                    marginBottom: '10px',
                                    marginTop: '10px'
                                }}>
                                    <div style={{flex: 1, textAlign: 'left'}}>ID</div>
                                    <div style={{flex: 1, textAlign: 'left'}}>이름</div>
                                    <div style={{flex: 1, textAlign: 'left'}}>가격</div>
                                    <div style={{flex: 1, textAlign: 'left'}}>관리</div>
                                </div>
                                <ul className="scrollable-card5" style={{listStyleType: 'none', padding: 0, maxHeight: '300px', overflowY: 'scroll'}}>
                                    {stocks.map(stock => (
                                        <li key={stock.id} style={{
                                            display: 'flex',
                                            justifyContent: 'space-between',
                                            alignItems: 'center',  // 요소들을 수직 중앙 정렬
                                            marginBottom: '5px',
                                            padding: '5px'
                                        }}>
                                            <div style={{flex: 1, textAlign: 'left'}}>{stock.id}</div>
                                            <div style={{flex: 1, textAlign: 'left'}}>{stock.stockName}</div>
                                            <div style={{flex: 1, textAlign: 'left'}}>{stock.currentPrice}</div>
                                            <div style={{flex: 1, textAlign: 'left'}}>
                                                <Button className="btn-icon" style={{marginLeft: 'auto'}} onClick={() => handleDeleteStock(stock.id)}>❌</Button>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </>
    );
};

export default Admin;
