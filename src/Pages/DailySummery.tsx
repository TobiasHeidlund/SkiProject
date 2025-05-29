import React, { useState, useEffect } from "react";
import axios from "axios";
import Run from "../Components/RunSummary";
import RunSummary from "../Components/RunSummary";

const backendUrl = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";

interface DailySummaryProps {
  total_altitude: number;
  total_distance: number;
  avg_speed: number;
  top_speed: number;
}

const DailySummary: React.FC = () => {
  const [selectedDate, setSelectedDate] = useState<string>(
    new Date().toISOString().split("T")[0]
  );

  const [summary, setSummary] = useState<DailySummaryProps>({
    total_altitude: 0,
    total_distance: 0,
    avg_speed: 0,
    top_speed: 0,
  });

  const [allowedDates, setAllowedDates] = useState<string[]>([]);

  const fetchSummary = async (date: string) => {
    try {
      const response = await axios.get(`${backendUrl}/daily/summery`, {
        params: { date },
      });
      console.log(response.data);
      setSummary(response.data);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.error("Axios error fetching summary data:"+ error.toJSON().toLocaleString());
      } else if (error instanceof Error) {
        console.error("Error fetching summary data:", error.message, error);
      } else {
        console.error("Unknown error fetching summary data:", error);
      }
    }
  };

  const fetchAllowedDates = async () => {
    try {
      const response = await axios.get(`${backendUrl}/daily/active-days`,);
      setAllowedDates(response.data); // Assuming the backend returns an array of allowed dates in 'YYYY-MM-DD' format
      console.log("Allowed dates:", response.data);
    } catch (error) {
      if (error instanceof Error) {
        console.error("Error fetching allowed dates:", error.message);
      } else {
        console.error("Error fetching allowed dates:", error);
      }
    }
  };

  useEffect(() => {
    // Fetch allowed dates for the current year and month when the component mounts
    fetchAllowedDates();
  }, []);

  useEffect(() => {
    if(allowedDates.length === 0) return; // Avoid running if allowedDates is empty
    // Fetch allowed dates for the current year and month when the component mounts
    setSelectedDate(allowedDates[allowedDates.length - 1]);
  }, [allowedDates]);

  useEffect(() => {
    // Fetch data for the current day when the component mounts or when the selected date changes
    fetchSummary(selectedDate);
  }, [selectedDate]);

  const handleDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = event.target.value;
    setSelectedDate(newDate);
  };

  const isDateAllowed = (date: string) => {
    return allowedDates.includes(date);
  };

  return (
    <div>
      <h1>Daily Summary</h1>
      <div>
        <div>
          <button
            onClick={() => {
              const currentIndex = allowedDates.indexOf(selectedDate);
              if (currentIndex > 0) {
            setSelectedDate(allowedDates[currentIndex - 1]);
              }
            }}
            disabled={allowedDates.indexOf(selectedDate) === 0}
          >
            Previous Day
          </button>
          <span style={{ margin: "0 10px" }}>{selectedDate}</span>
          <button
            onClick={() => {
              const currentIndex = allowedDates.indexOf(selectedDate);
              if (currentIndex < allowedDates.length - 1) {
            setSelectedDate(allowedDates[currentIndex + 1]);
              }
            }}
            disabled={allowedDates.indexOf(selectedDate) === allowedDates.length - 1}
          >
            Next Day
          </button>
        </div>
      </div>
      <div>
        <p>Total Altitude: {Math.round(summary.total_altitude)} m</p>
        <p>Total Distance: {Math.round(summary.total_distance)} km</p>
        <p>Average Speed: {Math.round(summary.avg_speed)} m/s</p>
        <p>Top Speed: {Math.round(summary.top_speed)} m/s</p>
      </div>
      <RunSummary date={selectedDate} />
    </div>
  );
};

export default DailySummary;