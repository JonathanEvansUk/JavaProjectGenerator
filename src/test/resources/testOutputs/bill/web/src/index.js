import ReactDOM from "react-dom/client";
import React from "react";
import { App } from "./App";
import { BrowserRouter } from "react-router-dom";

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

const app = document.getElementById("app");
const root = ReactDOM.createRoot(app);

root.render(
<BrowserRouter>
  <App />
</BrowserRouter>
);