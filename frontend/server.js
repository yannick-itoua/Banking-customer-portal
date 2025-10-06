import express from 'express';
import { createServer } from 'http';
import { parse } from 'url';
import next from 'next';

const dev = process.env.NODE_ENV !== 'production';
const hostname = process.env.HOSTNAME || '0.0.0.0';
const port = parseInt(process.env.PORT || '8080', 10);

// Initialize Next.js app
const app = next({ dev, hostname, port });
const handle = app.getRequestHandler();

app.prepare().then(() => {
  const server = express();

  // Health check endpoint for Railway
  server.get('/health', (req, res) => {
    res.json({ 
      status: 'healthy', 
      service: 'Banking Customer Portal Frontend',
      timestamp: new Date().toISOString()
    });
  });

  // Handle all other routes with Next.js
  server.all('*', (req, res) => {
    const parsedUrl = parse(req.url, true);
    return handle(req, res, parsedUrl);
  });

  const httpServer = createServer(server);

  httpServer.listen(port, hostname, () => {
    console.log(`🟢 Banking Customer Portal Frontend ready on http://${hostname}:${port}`);
    console.log(`🔗 Health check available at http://${hostname}:${port}/health`);
  });
});