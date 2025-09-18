'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';

export default function Home() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    console.log('Auth state:', { isAuthenticated, isLoading, user: isAuthenticated }); // Debug log
    
    if (!isLoading) {
      if (isAuthenticated) {
        console.log('Redirecting to dashboard'); // Debug log
        router.push('/dashboard');
      } else {
        console.log('Redirecting to login'); // Debug log
        router.push('/auth/login');
      }
    }
  }, [isAuthenticated, isLoading, router]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600 mx-auto"></div>
        <p className="mt-4 text-lg text-gray-600">Loading Banking Portal...</p>
      </div>
    </div>
  );
}