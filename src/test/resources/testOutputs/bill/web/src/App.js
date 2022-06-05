import { Container, Nav, Navbar, Breadcrumb } from "react-bootstrap";
import { NavLink, Route, Routes, Link } from "react-router-dom";
import Entities from "./routes/entities";
import Home from "./routes/home";
import CreateBill from "./routes/bill/CreateBill";
import BillList from "./routes/bill/BillList";
import ViewBill from "./routes/bill/ViewBill";
import EditBill from "./routes/bill/EditBill";

import useBreadcrumbs from "use-react-router-breadcrumbs";

export const App = () => {
  const breadcrumbs = useBreadcrumbs();
  return (
    <div>
      <Navbar bg="light">
        <Nav>
          <Nav.Link as={NavLink} to="/">
            Home
          </Nav.Link>
          <Nav.Link as={NavLink} to="/entities">
            Entities
          </Nav.Link>
        </Nav>
      </Navbar>

      <Breadcrumb>
        {breadcrumbs.map(({ match, breadcrumb }, i, {length}) =>
        <Breadcrumb.Item active={length - 1 === i} key={match.pathname} linkAs={Link} linkProps={ { to: match.pathname } }>{breadcrumb}</Breadcrumb.Item>
        )}
      </Breadcrumb>

      <Container className="py-2">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="entities" element={<Entities />} />
          <Route path="entities/bill/create" element={<CreateBill />} />
          <Route path="entities/bill" element={<BillList />} />
          <Route path="entities/bill/:id" element={<ViewBill />} />
          <Route path="entities/bill/:id/edit" element={<EditBill />} />
        </Routes>
      </Container>
    </div>
  );
};