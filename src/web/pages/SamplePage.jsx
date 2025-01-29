import React, { useState } from 'react';

const SamplePage = () => {
  const [response, setResponse] = useState(''); // AI's response
  const [loading, setLoading] = useState(false); // Loading state
  const [error, setError] = useState(null); // Error state

  // Function to handle API request
  const fetchAIResponse = async () => {
    setLoading(true);
    setError(null);

    const apiEndpoint = 'https://api.openai.com/v1/chat/completions';

    // Define hardcoded parameters for OpenAI API
    const requestBody = {
      model: 'gpt-4', // Specify the OpenAI model to use
      messages: [
        { role: 'system', content: 'You are a helpful assistant.' },
        { role: 'user', content: 'Write a poem about the sea.' }, // Replace with your desired input
      ],
      temperature: 0.7, // Controls the randomness of the output (0.7 is moderately random)
      max_tokens: 150, // Limits the length of the response
      top_p: 1.0, // Controls diversity via nucleus sampling
      frequency_penalty: 0.0, // Penalizes repeated phrases
      presence_penalty: 0.0, // Encourages discussion of new topics
    };

    try {
      const response = await fetch(apiEndpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${apiKey}`,
        },
        body: JSON.stringify(requestBody), // Send the hardcoded request body
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error.message || 'Failed to fetch AI response');
      }

      const data = await response.json();
      setResponse(data.choices[0].message.content.trim()); // Extract AI response
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>AI-Powered Graylog Insights</h1>
      <p>Click the button below to get AI-generated insights.</p>

      <div style={{ marginBottom: '10px' }}>
        <button onClick={fetchAIResponse} disabled={loading}>
          {loading ? 'Loading...' : 'Fetch AI Response'}
        </button>
      </div>

      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      {response && (
        <div style={{ marginTop: '20px', padding: '10px', border: '1px solid #ddd', backgroundColor: '#f9f9f9' }}>
          <h3>AI's Response:</h3>
          <p>{response}</p>
        </div>
      )}
    </div>
  );
};

export default SamplePage;
