
import './App.css'
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import DailySummary from "./Pages/DailySummery";
import RunDetails from "./Pages/RunDetails";

function App() {
    return (
      <Router>
      <Routes>
        <Route path="/" element={<DailySummary />}></Route>
        <Route path="/run/:runId" element={<RunDetails />} />
      </Routes>
    </Router>
  );
}

export default App
