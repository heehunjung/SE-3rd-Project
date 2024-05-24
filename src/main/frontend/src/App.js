import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Routes, Route } from 'react-router-dom';
import { Container } from 'react-bootstrap';

import Login from './components/Login';
import Join from "./pages/Join";
import Home from "./pages/Home";
function App() {
  return (
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/Home/:id" element={<Home />} />
        <Route path="/join" element={<Join />} />
      </Routes>
  );
};

export default App;
