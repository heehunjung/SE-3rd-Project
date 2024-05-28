import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Routes, Route } from 'react-router-dom';
import { Container } from 'react-bootstrap';

import Login from './components/Login';
import Join from "./pages/Join";
import Home from "./pages/Home";
import Board from "./pages/Board";
import Post from "./pages/Post";
import View from "./pages/View";
function App() {
  return (
      <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/Home/:id" element={<Home />} />
          <Route path="/join" element={<Join />} />
          <Route path="/Board/:id" element={<Board />} />
          <Route path="/Post/:id" element={<Post />} />
          <Route path="/ViewPost/:id" element={<View />} />
      </Routes>
  );
};

export default App;
